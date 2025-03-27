package ru.mwg.node.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.mwg.node.entyty.AppDocument;
import ru.mwg.node.repository.AppDocumentRepository;

@Service
public class FileService {

  @Value("${config.bot.token}")
  private String token;

  @Value("${config.bot.file_info.uri}")
  private String fileInfoUri;

  @Value("${config.bot.file_storage.uri}")
  private String fileStorageUri;

  private final AppDocumentRepository appDocumentRepository;

  public FileService(
      AppDocumentRepository appDocumentRepository
  ) {
    this.appDocumentRepository = appDocumentRepository;
  }

  public AppDocument processDoc(Message telegramMessage) {
    String fileId = telegramMessage.getDocument().getFileId();
    ResponseEntity<String> response = getFilePath(fileId);
    if (response.getStatusCode() == HttpStatus.OK) {
      JSONObject jsonObject = new JSONObject(response.getBody());
      String filePath = String.valueOf(jsonObject
          .getJSONObject("result")
          .getString("file_path"));
      byte[] fileInByte = downloadFile(filePath);

      Document telegramDoc = telegramMessage.getDocument();
      AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, fileInByte);
      return appDocumentRepository.save(transientAppDoc);
    } else {
      throw new RuntimeException("Bad response from telegram service: " + response);
    }
  }

  private AppDocument buildTransientAppDoc(Document telegramDoc, byte[] persistentBinaryContent) {
    return AppDocument.builder()
        .telegramFileId(telegramDoc.getFileId())
        .docName(telegramDoc.getFileName())
        .binaryContent(persistentBinaryContent)
        .mimeType(telegramDoc.getMimeType())
        .fileSize(telegramDoc.getFileSize())
        .build();
  }

  private ResponseEntity<String> getFilePath(String fileId) {
    RestTemplate restTemplate = new RestTemplate();
    HttpHeaders headers = new HttpHeaders();
    HttpEntity<String> request = new HttpEntity<>(headers);
    String fullUri = fileStorageUri.replace("{token}", token)
        .replace("{fileId}", fileId);
    return restTemplate.exchange(
        fullUri,
        HttpMethod.GET,
        request,
        String.class
    );
  }

  private byte[] downloadFile(String filePath) {
    String fullUri = fileInfoUri.replace("{token}", token)
        .replace("{filePath}", filePath);
    URL urlObj;
    try {
      urlObj = new URL(fullUri);
    } catch (MalformedURLException e) {
      throw new RuntimeException(e);
    }

    //TODO подумать над оптимизацией
    try (InputStream is = urlObj.openStream()) {
      return is.readAllBytes();
    } catch (IOException e) {
      throw new RuntimeException(urlObj.toExternalForm(), e);
    }
  }
}
