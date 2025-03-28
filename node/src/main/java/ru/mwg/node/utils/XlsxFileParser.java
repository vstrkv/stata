package ru.mwg.node.utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import ru.mwg.node.entyty.Player;

public class XlsxFileParser {

  public static List<Player> getListPlayers(byte[] data) throws IOException {
    InputStream file = new ByteArrayInputStream(data);

    XSSFWorkbook workbook = new XSSFWorkbook(file);
    XSSFSheet sheet = workbook.getSheetAt(0);
    Iterator<Row> rowIterator = sheet.iterator();
    checkCorrectColumn(rowIterator);
    List<Player> players = new ArrayList<>();
    while (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      Iterator<Cell> cellIterator = row.cellIterator();
      int count = 0;
      Player e = new Player();
      while (cellIterator.hasNext()) {
        Cell cell = cellIterator.next();
        if (count == 0) {
          e.setNumber((int) cell.getNumericCellValue());
        } else if (count == 1) {
          e.setGameName(getValueStr(cell));
        } else if (count == 2) {
          e.setRealName(getValueStr(cell));
        } else if (count == 3) {
          e.setPhone(getValueStr(cell));
        } else if (count == 4) {
          e.setDateCreate(getValueStr(cell));
        } else if (count == 5) {
          String s = getValueStr(cell);
          if (s != null && s.length() > 0) {
            e.setDateFirstPlay(LocalDate.parse(s));
          }
        } else if (count == 6) {
          e.setTypeGame(getValueStr(cell));
        } else if (count == 7) {
          String s = getValueStr(cell);
          if (s != null && s.length() > 0) {
            e.setDateLastGame(LocalDate.parse(s));
          }
        } else if (count == 8) {
          e.setTgName(getValueStr(cell));
        } else if (count == 9) {
          e.setCountGame(getValueStr(cell));
        } else if (count == 10) {
          e.setSpendMoney(cell.getNumericCellValue());
        } else if (count == 11) {
          e.setCountEvent((int) cell.getNumericCellValue());
        }
        count++;
      }
      players.add(e);
    }
    return players;
  }


  private static void checkCorrectColumn(Iterator<Row> rowIterator) {
    if (rowIterator.hasNext()) {
      Row row = rowIterator.next();
      Iterator<Cell> cellIterator = row.cellIterator();
      checkField(cellIterator, "№");
      checkField(cellIterator, "Игровое имя");
      checkField(cellIterator, "Настоящее имя");
      checkField(cellIterator, "Телефон");
      checkField(cellIterator, "Дата создания");
      checkField(cellIterator, "Дата первой афиши");
      checkField(cellIterator, "Тип первой афиши");
      checkField(cellIterator, "Дата последней афиши");
      checkField(cellIterator, "Telegram");
      checkField(cellIterator, "Кол-во игр");
      checkField(cellIterator, "Потрачено денег");
      checkField(cellIterator, "Кол-во афиш");
    }
  }

  private static void checkField(Iterator<Cell> cellIterator, String s) {
    if (cellIterator.hasNext()) {
      String cl = getValueStr(cellIterator.next());
      if (!s.equals(cl)) {
        throw new RuntimeException(String.format("error {} and {}" , s, cl));
      }
    }
  }

  private static String getValueStr(Cell cell) {
    try {
      return cell.getStringCellValue();
    } catch (Exception e) {
      return String.valueOf(cell.getNumericCellValue());
    }
  }
}
