package com.ofsms.tests;

import io.qameta.allure.Allure;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.collections.Lists;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TestReportListener implements ITestListener {
    private static final DateTimeFormatter TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");
    private static final Path SCREENSHOT_DIR = Path.of("target", "screenshots");
    private static final Path REPORT_DIR = Path.of("target", "reports");
    private static final Path SUMMARY_REPORT = REPORT_DIR.resolve("test-summary.txt");
    private static final Path PDF_SUMMARY_REPORT = REPORT_DIR.resolve("test-summary.pdf");
    private static final float PDF_FONT_SIZE = 10f;
    private static final float PDF_LEADING = 14f;
    private static final float PDF_MARGIN = 50f;

    @Override
    public void onStart(ITestContext context) {
        createScreenshotDirectory();
        createReportDirectory();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        captureScreenshot(result, "passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        captureScreenshot(result, "failed");
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        captureScreenshot(result, "skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        List<String> reportLines = buildSummaryLines(context);
        writeTextSummaryReport(reportLines);
        writePdfSummaryReport(reportLines);
    }

    private void createScreenshotDirectory() {
        try {
            Files.createDirectories(SCREENSHOT_DIR);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to create screenshot directory", e);
        }
    }

    private void createReportDirectory() {
        try {
            Files.createDirectories(REPORT_DIR);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to create report directory", e);
        }
    }

    private void captureScreenshot(ITestResult result, String status) {
        Object instance = result.getInstance();
        if (!(instance instanceof BaseTest)) {
            return;
        }

        WebDriver driver = ((BaseTest) instance).getDriver();
        if (!(driver instanceof TakesScreenshot)) {
            return;
        }

        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        String screenshotName = result.getTestClass().getRealClass().getSimpleName()
                + "-" + result.getMethod().getMethodName()
                + "-" + status
                + "-" + TIMESTAMP.format(LocalDateTime.now()) + ".png";

        try {
            Files.write(SCREENSHOT_DIR.resolve(screenshotName), screenshot);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to save screenshot for test result", e);
        }

        Allure.addAttachment(status + " screenshot: " + screenshotName, new ByteArrayInputStream(screenshot));
    }

    private List<String> buildSummaryLines(ITestContext context) {
        List<ITestResult> testResults = Lists.newArrayList();
        testResults.addAll(context.getPassedTests().getAllResults());
        testResults.addAll(context.getFailedTests().getAllResults());
        testResults.addAll(context.getSkippedTests().getAllResults());
        testResults.sort(Comparator
                .comparing((ITestResult result) -> result.getTestClass().getRealClass().getSimpleName())
                .thenComparing(result -> result.getMethod().getMethodName()));

        List<String> lines = Lists.newArrayList();
        lines.add("OFSMS Automation Test Summary");
        lines.add("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        lines.add("Test context: " + context.getName());
        lines.add("Total tests: " + testResults.size());
        lines.add("Passed: " + context.getPassedTests().size());
        lines.add("Failed: " + context.getFailedTests().size());
        lines.add("Skipped: " + context.getSkippedTests().size());
        lines.add("");
        lines.add(String.format("%-28s %-45s %-10s", "Class", "Test Name", "Status"));
        lines.add(String.format("%-28s %-45s %-10s", "-----", "---------", "------"));

        for (ITestResult result : testResults) {
            lines.add(String.format("%-28s %-45s %-10s",
                    result.getTestClass().getRealClass().getSimpleName(),
                    result.getMethod().getMethodName(),
                    resolveStatus(result)));
        }

        return lines;
    }

    private void writeTextSummaryReport(List<String> lines) {
        try {
            Files.write(SUMMARY_REPORT, lines, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to save text summary report", e);
        }
    }

    private void writePdfSummaryReport(List<String> lines) {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);

            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            contentStream.setFont(PDType1Font.COURIER, PDF_FONT_SIZE);
            contentStream.beginText();
            contentStream.newLineAtOffset(PDF_MARGIN, page.getMediaBox().getHeight() - PDF_MARGIN);

            float yPosition = page.getMediaBox().getHeight() - PDF_MARGIN;
            for (String line : normalizePdfLines(lines)) {
                if (yPosition <= PDF_MARGIN) {
                    contentStream.endText();
                    contentStream.close();
                    page = new PDPage(PDRectangle.LETTER);
                    document.addPage(page);
                    contentStream = new PDPageContentStream(document, page);
                    contentStream.setFont(PDType1Font.COURIER, PDF_FONT_SIZE);
                    contentStream.beginText();
                    contentStream.newLineAtOffset(PDF_MARGIN, page.getMediaBox().getHeight() - PDF_MARGIN);
                    yPosition = page.getMediaBox().getHeight() - PDF_MARGIN;
                }

                contentStream.showText(line);
                contentStream.newLineAtOffset(0, -PDF_LEADING);
                yPosition -= PDF_LEADING;
            }

            contentStream.endText();
            contentStream.close();
            document.save(PDF_SUMMARY_REPORT.toFile());
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to save PDF summary report", e);
        }
    }

    private List<String> normalizePdfLines(List<String> lines) {
        List<String> normalizedLines = new ArrayList<>();
        for (String line : lines) {
            if (line.isEmpty()) {
                normalizedLines.add(" ");
                continue;
            }

            int start = 0;
            int maxLineLength = 95;
            while (start < line.length()) {
                int end = Math.min(start + maxLineLength, line.length());
                normalizedLines.add(line.substring(start, end));
                start = end;
            }
        }
        return normalizedLines;
    }

    private String resolveStatus(ITestResult result) {
        switch (result.getStatus()) {
            case ITestResult.SUCCESS:
                return "PASSED";
            case ITestResult.FAILURE:
                return "FAILED";
            case ITestResult.SKIP:
                return "SKIPPED";
            default:
                return "UNKNOWN";
        }
    }
}
