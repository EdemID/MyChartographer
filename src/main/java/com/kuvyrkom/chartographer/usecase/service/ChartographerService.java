package com.kuvyrkom.chartographer.usecase.service;

import com.kuvyrkom.chartographer.adapter.persistence.service.ChartaServiceImpl;
import com.kuvyrkom.chartographer.domain.model.Charta;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ChartographerService {

    private ChartaServiceImpl chartaService;

    public ChartographerService(ChartaServiceImpl chartaService) {
        this.chartaService = chartaService;
    }

    public String createNewCharta(int width, int height) throws IOException {
        String fileUUID = UUID.randomUUID().toString();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        String filePath = "src/main/resources/graph/" + fileUUID + ".bmp";
        File output = new File(filePath);
        ImageIO.write(bufferedImage, "bmp", output);

        Charta charta = new Charta(width, height, fileUUID, filePath);
        chartaService.save(charta);
        return fileUUID;
    }

    public void saveRestoredFragmentCharta(String id, int x, int y, int width, int height, MultipartFile multipartFile) throws Exception {
        File chartaFile = new File(chartaService.findByFileUUID(id).getFilePath());
        BufferedImage chartaImage = ImageIO.read(chartaFile);

        BufferedImage image = ImageIO.read(multipartFile.getInputStream());
        BufferedImage fragment = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics fragmentGraphics = fragment.getGraphics();
        /** удалить*/
        fragmentGraphics.setColor(Color.YELLOW);
        fragmentGraphics.fillRect(0, 0, width, height);
        /***/
        fragmentGraphics.drawImage(image, 0, 0, null);

        Graphics chartaGraphics = chartaImage.getGraphics();
        chartaGraphics.drawImage(fragment, x, y, null);

        ImageIO.write(chartaImage, "bmp", chartaFile);
    }

    public byte[] getRestoredPartOfCharta(String id, int x, int y, int width, int height) throws Exception {
        ByteArrayOutputStream imageBytes = new ByteArrayOutputStream();

        File chartaFile = new File(chartaService.findByFileUUID(id).getFilePath());
        BufferedImage charta = ImageIO.read(chartaFile);
        int widthCharta = charta.getWidth();
        int heightCharta = charta.getHeight();

        BufferedImage restoredPartOfCharta = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        if (x + width > widthCharta || y + height > heightCharta) {
            charta = charta.getSubimage(x, y, widthCharta - x, heightCharta - y);

            Graphics restoredPartOfChartaGraphics = restoredPartOfCharta.getGraphics();
            /** удалить*/
            restoredPartOfChartaGraphics.setColor(Color.YELLOW);
            restoredPartOfChartaGraphics.fillRect(0, 0, width, height);
            /***/
            restoredPartOfChartaGraphics.drawImage(charta, 0, 0, null);
        } else {
            restoredPartOfCharta = charta.getSubimage(x, y, width, height);
        }

        ImageIO.write(restoredPartOfCharta, "bmp", imageBytes);

        return imageBytes.toByteArray();
    }
}
