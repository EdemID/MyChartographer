package com.kuvyrkom.chartographer.infrastructure.util;

import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaLockServiceImpl;
import com.kuvyrkom.chartographer.usecase.exception.ChartaLockedException;
import com.kuvyrkom.chartographer.usecase.exception.ChartaNotFoundException;
import com.kuvyrkom.chartographer.usecase.service.ChartographerService;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

/**
 * Утильный класс
 */
public class ChartaUtil {

    /**
     * Проверяет статус блокирования чарты
     * Необходимо для того, чтобы невозможно было пользоваться одной чартой одновременно
     *
     * @param chartaLockService
     * @param fileUUID
     */
    public static void checkForLocking(ChartaLockServiceImpl chartaLockService, String fileUUID) {
        if (chartaLockService.isLocked(fileUUID)) {
            throw new ChartaLockedException(fileUUID);
        }
    }

    /**
     * Проверяет на существование
     *
     * @param chartaOriginal
     * @param fileUUID
     */
    public static void checkForExistence(BufferedImage chartaOriginal, String fileUUID) {
        if (chartaOriginal == null) {
            throw new ChartaNotFoundException(fileUUID);
        }
    }

    /**
     * Сжимает и сохраняет на диск чарту
     *
     * @param image             буфферизированная чарта
     * @param filePath          путь до файла чарты
     * @param ex                расширение, в котором необходимо сохранить чарту
     * @throws IOException
     */
    public static void compressAndWriteToFile(BufferedImage image, String filePath, String ex) throws IOException {
        File compressedImageFile = new File(filePath);
        OutputStream os = new FileOutputStream(compressedImageFile);

        Iterator<ImageWriter> writers =  ImageIO.getImageWritersByFormatName(ex);
        ImageWriter writer = (ImageWriter) writers.next();
        ImageOutputStream ios = ImageIO.createImageOutputStream(os);
        writer.setOutput(ios);
        ImageWriteParam param = writer.getDefaultWriteParam();
        param.setCompressionMode(ImageWriteParam.MODE_DEFAULT);
        writer.write(null, new IIOImage(image, null, null), param);

        os.close();
        ios.close();
        writer.dispose();
    }

    /**
     * Создание временной директории, в котором сервис сможет хранить данные. Для теста
     *
     * @param args  ожидаемый элемент массива является переданная строка-путь при запуске приложения
     */
    public static void createTmpChartaDirectory(String[] args) {
        File tmpChartaDirectory;
        if (args.length == 0) {
            tmpChartaDirectory = new File("target/imageCharta/");
            ChartographerService.tmpChartaDirectory = tmpChartaDirectory.getPath();
        } else {
            String path = args[0];
            tmpChartaDirectory = new File(path);
            ChartographerService.tmpChartaDirectory = path;
        }
        tmpChartaDirectory.mkdir();
    }
}
