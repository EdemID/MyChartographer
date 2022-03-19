package com.kuvyrkom.chartographer.usecase.service;

import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaLockServiceImpl;
import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaServiceImpl;
import com.kuvyrkom.chartographer.domain.model.Charta;
import com.kuvyrkom.chartographer.infrastructure.util.ChartaUtil;
import com.kuvyrkom.chartographer.usecase.exception.BigChartaAlreadyCreatedException;
import org.apache.commons.imaging.Imaging;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.util.UUID;

import static com.kuvyrkom.chartographer.infrastructure.util.ChartaUtil.compressAndWriteToFile;

/**
 * Класс бизнес логики
 */
@Service
public class ChartographerService {

    private static final String EX_CHARTA = "jpg";
    private static final String EX_FRAGMENT = "bmp";

    public static String tmpChartaDirectory;
    private static BufferedImage bigChartaOriginal;
    private static String bigFileUUID;

    private ChartaServiceImpl chartaService;
    private ChartaLockServiceImpl chartaLockService;

    public ChartographerService(ChartaServiceImpl chartaService, ChartaLockServiceImpl chartaLockService) {
        this.chartaService = chartaService;
        this.chartaLockService = chartaLockService;
    }

    /**
     * Создание новой чарты
     * Создание чарты высотой от 20000 ограничено в количестве единицы для избежания Java heap space
     *
     * @param width     ширина чарты
     * @param height    высота чарты
     * @return          возвращает fileUUID чарты
     */
    public String createNewCharta(int width, int height) throws IOException {
        String fileUUID = UUID.randomUUID().toString();
        String filePath = tmpChartaDirectory + FileSystems.getDefault().getSeparator() + fileUUID + "." + EX_CHARTA;

        if (bigChartaOriginal != null && height > 20000) {
            throw new BigChartaAlreadyCreatedException(bigFileUUID);
        }

        BufferedImage chartaOriginal;
        if (height > 20000) {
            bigFileUUID = fileUUID;
            bigChartaOriginal = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            chartaOriginal = bigChartaOriginal;
        } else {
            chartaOriginal = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        }
        compressAndWriteToFile(chartaOriginal, filePath, EX_CHARTA);

        Charta charta = new Charta(width, height, fileUUID, filePath);
        chartaService.save(charta);
        chartaLockService.insertLockingInfo(fileUUID);

        return fileUUID;
    }

    /**
     * Сохранение восстановленного фрагмента изображения
     *
     * @param id                fileUUID чарты
     * @param x                 ось абсцисс
     * @param y                 ось ордината
     * @param width             ширина фрагмента
     * @param height            высота фрагмента
     * @param multipartFile     переданный фрагмент изображения
     */
    public void saveRestoredFragmentCharta(String id, int x, int y, int width, int height, MultipartFile multipartFile) throws Exception {
        ChartaUtil.checkForLocking(chartaLockService, id);
        chartaLockService.lockByFileUUID(id);

        Charta charta = chartaService.findByFileUUID(id);
        String filePath = charta.getFilePath();
        File fileCharta = new File(filePath);
        int chartaHeight = charta.getHeight();

        BufferedImage chartaOriginal;
        if (chartaHeight > 20000) {
            chartaOriginal = bigChartaOriginal;
        } else {
            chartaOriginal = Imaging.getBufferedImage(new File(filePath));
        }
        ChartaUtil.checkForExistence(chartaOriginal, id);

        BufferedImage transferredImageFragment = ImageIO.read(multipartFile.getInputStream());
        BufferedImage fragmentToSave = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        try {
            Graphics fragmentGraphics = fragmentToSave.getGraphics();
            fragmentGraphics.drawImage(transferredImageFragment, 0, 0, null);
            Graphics chartaGraphics = chartaOriginal.getGraphics();
            chartaGraphics.drawImage(fragmentToSave, x, y, null);
            ImageIO.write(chartaOriginal, EX_CHARTA, fileCharta);

            fragmentGraphics.dispose();
            chartaGraphics.dispose();
            fragmentGraphics = null;
            chartaGraphics = null;
        } finally {
            fragmentToSave.flush();
            transferredImageFragment.flush();
            fragmentToSave = null;
            transferredImageFragment = null;
            System.gc();
            chartaLockService.unlockByFileUUID(id);
        }
    }

    /**
     * Получение восстановленной части изображения
     *
     * @param id                fileUUID чарты
     * @param x                 ось абсцисс
     * @param y                 ось ордината
     * @param fragmentWidth     ширина фрагмента
     * @param fragmentHeight    высота фрагмента
     * @return byte[]           возвращает изображение в байтовом представлении
     */
    public byte[] getRestoredPartOfCharta(String id, int x, int y, int fragmentWidth, int fragmentHeight) throws Exception {
        ChartaUtil.checkForLocking(chartaLockService, id);
        chartaLockService.lockByFileUUID(id);

        Charta charta = chartaService.findByFileUUID(id);
        int chartaHeight = charta.getHeight();
        BufferedImage chartaOriginal;
        if (chartaHeight > 20000) {
            chartaOriginal = bigChartaOriginal;
        } else {
            String path = charta.getFilePath();
            chartaOriginal = Imaging.getBufferedImage(new File(path));
        }
        ChartaUtil.checkForExistence(chartaOriginal, id);

        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedImage bufferedCharta = chartaOriginal;
            int bufferedChartaWidth = bufferedCharta.getWidth();
            int bufferedChartaHeight = bufferedCharta.getHeight();
            BufferedImage restoredPartOfCharta = new BufferedImage(fragmentWidth, fragmentHeight, BufferedImage.TYPE_INT_RGB);
            Graphics restoredPartOfChartaGraphics = restoredPartOfCharta.getGraphics();
            if (x + fragmentWidth > bufferedChartaWidth && y + fragmentHeight > bufferedChartaHeight) {
                bufferedCharta = bufferedCharta.getSubimage(x, y, bufferedChartaWidth - x, bufferedChartaHeight - y);
            } else if (x + fragmentWidth > bufferedChartaWidth) {
                bufferedCharta = bufferedCharta.getSubimage(x, y, bufferedChartaWidth - x, fragmentHeight);
            } else if (y + fragmentHeight > bufferedChartaHeight) {
                bufferedCharta = bufferedCharta.getSubimage(x, y, fragmentWidth, bufferedChartaHeight - y);
            } else {
                bufferedCharta = bufferedCharta.getSubimage(x, y, fragmentWidth, fragmentHeight);
            }
            restoredPartOfChartaGraphics.drawImage(bufferedCharta, 0, 0, null);
            restoredPartOfChartaGraphics.dispose();
            ImageIO.write(restoredPartOfCharta, EX_FRAGMENT, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();
            bufferedCharta.flush();
            restoredPartOfCharta.flush();
            byteArrayOutputStream = null;
            bufferedCharta = null;
            restoredPartOfCharta = null;
            restoredPartOfChartaGraphics = null;
            System.gc();

            return imageBytes;
        } finally {
            chartaLockService.unlockByFileUUID(id);
        }
    }

    public void deleteCharta(String id) {
        Charta charta = chartaService.findByFileUUID(id);
        String filePath = charta.getFilePath();
        File fileCharta = new File(filePath);

        if (fileCharta.delete()) {
            chartaService.delete(charta);
            chartaLockService.deleteByFileUUID(id);
        }

        int chartaHeight = charta.getHeight();
        if (chartaHeight > 20000) {
            bigChartaOriginal.flush();
            bigChartaOriginal = null;
            bigFileUUID = null;
        }
    }
}
