package com.kuvyrkom.chartographer.usecase.service;

import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaLockServiceImpl;
import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaServiceImpl;
import com.kuvyrkom.chartographer.domain.model.Charta;
import com.kuvyrkom.chartographer.infrastructure.util.ChartaUtil;
import com.kuvyrkom.chartographer.usecase.exception.ChartaAlreadyCreatedException;
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

@Service
public class ChartographerService {

    private static final String EX_CHARTA = "jpg";
    private static final String EX_FRAGMENT = "bmp";

    public static String tmpChartaDirectory;
    private static BufferedImage chartaOriginal;
    private static String fileUUID;
    private static File fileCharta;

    private ChartaServiceImpl chartaService;
    private ChartaLockServiceImpl chartaLockService;

    public ChartographerService(ChartaServiceImpl chartaService, ChartaLockServiceImpl chartaLockService) {
        this.chartaService = chartaService;
        this.chartaLockService = chartaLockService;
    }

    public String createNewCharta(int width, int height) throws IOException {
        if (chartaOriginal != null) {
            throw new ChartaAlreadyCreatedException(fileUUID);
        }
        fileUUID = UUID.randomUUID().toString();
        chartaOriginal = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        String filePath = tmpChartaDirectory + FileSystems.getDefault().getSeparator() + fileUUID + "." + EX_CHARTA;
        fileCharta = new File(filePath);
        compressAndWriteToFile(chartaOriginal, filePath, EX_CHARTA);

        Charta charta = new Charta(width, height, fileUUID, filePath);
        chartaService.save(charta);
        chartaLockService.insertLockingInfo(fileUUID);

        return fileUUID;
    }

    public void saveRestoredFragmentCharta(String id, int x, int y, int width, int height, MultipartFile multipartFile) throws Exception {
        ChartaUtil.checkForExistenceAndLocking(chartaOriginal, chartaLockService, id);
        chartaLockService.lockByFileUUID(id);

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
        } finally {
            fragmentToSave.flush();
            transferredImageFragment.flush();
            fragmentToSave = null;
            transferredImageFragment = null;
            chartaLockService.unlockByFileUUID(id);
        }
    }

    public byte[] getRestoredPartOfCharta(String id, int x, int y, int fragmentWidth, int fragmentHeight) throws Exception {
        ChartaUtil.checkForExistenceAndLocking(chartaOriginal, chartaLockService, id);
        chartaLockService.lockByFileUUID(id);
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            BufferedImage charta = chartaOriginal;
            int chartaWidth = charta.getWidth();
            int chartaHeight = charta.getHeight();
            BufferedImage restoredPartOfCharta = new BufferedImage(fragmentWidth, fragmentHeight, BufferedImage.TYPE_INT_RGB);
            Graphics restoredPartOfChartaGraphics = restoredPartOfCharta.getGraphics();
            if (x + fragmentWidth > chartaWidth && y + fragmentHeight > chartaHeight) {
                System.out.println(1);
                charta = charta.getSubimage(x, y, chartaWidth - x, chartaHeight - y);
            } else if (x + fragmentWidth > chartaWidth) {
                System.out.println(2);
                charta = charta.getSubimage(x, y, chartaWidth - x, fragmentHeight);
            } else if (y + fragmentHeight > chartaHeight) {
                System.out.println(3);
                charta = charta.getSubimage(x, y, fragmentWidth, chartaHeight - y);
            } else {
                charta = charta.getSubimage(x, y, fragmentWidth, fragmentHeight);
            }
            restoredPartOfChartaGraphics.drawImage(charta, 0, 0, null);
            ImageIO.write(restoredPartOfCharta, EX_FRAGMENT, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            byteArrayOutputStream.close();
            charta.flush();
            restoredPartOfCharta.flush();

            return imageBytes;
        } finally {
            chartaLockService.unlockByFileUUID(id);
        }
    }

    public void deleteCharta(String id) {
        Charta charta = chartaService.findByFileUUID(id);
        fileCharta.delete();
        chartaService.delete(charta);
        chartaLockService.deleteByFileUUID(id);

        chartaOriginal.flush();
        chartaOriginal = null;
        fileUUID = null;
        fileCharta = null;
    }
}
