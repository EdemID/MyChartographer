package com.kuvyrkom.chartographer.infrastructure.util;

import com.kuvyrkom.chartographer.adapter.persistence.exception.ChartaNotFoundException;
import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaLockServiceImpl;
import com.kuvyrkom.chartographer.usecase.exception.ChartaLockedException;
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

public class ChartaUtil {

    public static void checkForExistenceAndLocking(BufferedImage chartaOriginal, ChartaLockServiceImpl chartaLockService, String fileUUID) {
        if (chartaOriginal == null) {
            throw new ChartaNotFoundException(fileUUID);
        }
        if (chartaLockService.isLocked(fileUUID)) {
            throw new ChartaLockedException(fileUUID);
        }
    }

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

    public static void createTmpChartaDirectory(String[] args) {
        File tmpChartaDirectory;
        if (args.length == 0) {
            tmpChartaDirectory = new File("target/imageCharta/");
        } else {
            tmpChartaDirectory = new File(args[0]);
        }
        tmpChartaDirectory.mkdir();

        ChartographerService.tmpChartaDirectory = tmpChartaDirectory.getPath();
    }
}
