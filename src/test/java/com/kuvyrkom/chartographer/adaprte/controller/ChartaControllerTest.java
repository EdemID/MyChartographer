package com.kuvyrkom.chartographer.adaprte.controller;

import com.kuvyrkom.chartographer.adapter.restapi.controller.ChartaController;
import com.kuvyrkom.chartographer.usecase.application.BaseTest;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.validation.ConstraintViolationException;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

public class ChartaControllerTest extends BaseTest {

    private ChartaController chartaController;

    @Autowired
    public ChartaControllerTest(ChartaController chartaController) {
        this.chartaController = chartaController;
    }

    @Test
    @DisplayName("Проверить создание чарты с превосходящей шириной")
    public void negativeCreateNewChartaWithInvalidWidth() throws Exception {
        try {
            chartaController.createNewCharta(20001, 1);
        }
        catch (ConstraintViolationException e) {
            String expectedExceptionText = "createNewCharta.width: должно быть не больше 20000";
            Assertions.assertEquals(expectedExceptionText, e.getMessage(), "Невалидная ширина прошла проверку");
        }
    }

    @Test
    @DisplayName("Проверить создание чарты с превосходящей высотой")
    public void negativeCreateNewChartaWithInvalidHeight() throws Exception {
        try {
            chartaController.createNewCharta(1, 50001);
        }
        catch (ConstraintViolationException e) {
            String expectedExceptionText = "createNewCharta.height: должно быть не больше 50000";
            Assertions.assertEquals(expectedExceptionText, e.getMessage(), "Невалидная высота прошла проверку");
        }
    }

    @Test
    @DisplayName("Проверить создание чарты")
    public void createNewCharta() throws Exception {
        ResponseEntity<String> responseEntity = chartaController.createNewCharta(1, 1);
        Assertions.assertEquals("201 CREATED", responseEntity.getStatusCode().toString(), "Код отличный от 201 CREATED");
    }

    @Test
    @DisplayName("Проверить сохранение восстановленного фрагмента изображения")
    public void saveRestoredFragmentCharta() throws Exception {
        ResponseEntity<String> responseEntityCreateCharta = chartaController.createNewCharta(1, 1);
        String fileUUID = responseEntityCreateCharta.getBody();

        File file = new File("src/test/resources/image/transferredImageFragment.bmp");
        FileInputStream input = new FileInputStream(file);
        MultipartFile transferredImageFragment = new MockMultipartFile("transferredImageFragment.bmp",
                file.getName(), "image/bmp", IOUtils.toByteArray(input));

        ResponseEntity<String> responseEntity = chartaController.saveRestoredFragmentCharta(fileUUID, 1, 1, 2, 2, transferredImageFragment);
        Assertions.assertEquals("200 OK", responseEntity.getStatusCode().toString(), "Код отличный от 200 OK");
    }

    @Test
    @DisplayName("Проверить получение восстановленной части изображения с превосходящей шириной")
    public void negativeGetRestoredPartOfChartaWithInvalidWidth() throws Exception {
        try {
            ResponseEntity<String> responseEntityCreateCharta = chartaController.createNewCharta(1920, 1080);
            String fileUUID = responseEntityCreateCharta.getBody();

            chartaController.getRestoredPartOfCharta(fileUUID, 0, 0, 5001, 1);
        }
        catch (ConstraintViolationException e) {
            String expectedExceptionText = "getRestoredPartOfCharta.width: должно быть не больше 5000";
            Assertions.assertEquals(expectedExceptionText, e.getMessage(), "Невалидная ширина прошла проверку");
        }
    }

    @Test
    @DisplayName("Проверить получение восстановленной части изображения с превосходящей высотой")
    public void negativeGetRestoredPartOfChartaWithInvalidHeight() throws Exception {
        try {
            ResponseEntity<String> responseEntityCreateCharta = chartaController.createNewCharta(1920, 1080);
            String fileUUID = responseEntityCreateCharta.getBody();

            chartaController.getRestoredPartOfCharta(fileUUID, 0, 0, 1, 5001);
        }
        catch (ConstraintViolationException e) {
            String expectedExceptionMessage = "getRestoredPartOfCharta.height: должно быть не больше 5000";
            Assertions.assertEquals(expectedExceptionMessage, e.getMessage(), "Невалидная высота прошла проверку");
        }
    }

    @Test
    @DisplayName("Проверить получение восстановленной части изображения")
    public void getRestoredPartOfCharta() throws Exception {
        ResponseEntity<String> responseEntityCreateCharta = chartaController.createNewCharta(1920, 1080);
        String fileUUID = responseEntityCreateCharta.getBody();

        File file = new File("src/test/resources/image/transferredImageFragment.bmp");
        FileInputStream input = new FileInputStream(file);
        MultipartFile transferredImageFragment = new MockMultipartFile("transferredImageFragment.bmp",
                file.getName(), "image/bmp", IOUtils.toByteArray(input));

        chartaController.saveRestoredFragmentCharta(fileUUID, 0, 0, 1920, 1080, transferredImageFragment);

        int setWidth = 700;
        int setHeight = 710;
        ResponseEntity<byte[]> responseEntity = chartaController.getRestoredPartOfCharta(fileUUID, 600, 200, setWidth, setHeight);
        Assertions.assertEquals("200 OK", responseEntity.getStatusCode().toString(), "Код отличный от 200 OK");

        byte[] dataFragment = responseEntity.getBody();
        if (dataFragment != null) {
            ByteArrayInputStream bis = new ByteArrayInputStream(dataFragment);
            BufferedImage fragment = ImageIO.read(bis);

            int fragmentWidth = fragment.getWidth();
            int fragmentHeight = fragment.getHeight();
            boolean hasAlpha = fragment.getColorModel().hasAlpha();
            int pixelSize = fragment.getColorModel().getPixelSize();
            int type = fragment.getType();
            int expectedPixelSize = 24;
            int rgbType = 5;
            Assertions.assertEquals(setWidth, fragmentWidth, "Задаваемая ширина " + setWidth + " не совпадает с шириной фрагмента - " + fragmentWidth);
            Assertions.assertEquals(setHeight, fragmentHeight, "Задаваемая высота " + setHeight + " не совпадает с высотой фрагмента - " + fragmentHeight);
            Assertions.assertFalse(hasAlpha, "Фрагмент имеет альфа-канал");
            Assertions.assertEquals(expectedPixelSize, pixelSize, "В одном пикселе не 24 бита");
            Assertions.assertEquals(rgbType, type, "Цветность не TYPE_3BYTE_BGR");
        }
    }
}
