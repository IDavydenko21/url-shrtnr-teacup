package edu.kpi.testcourse.dataservice;

import com.google.gson.Gson;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;

@Singleton
class DataServiceImpl implements DataService {
  private final String jsonFileExtension = ".json";
  private final String userFileExtension = ".usr";
  private final String rootPath = "./.data";

  public DataServiceImpl() {
    createDirectory(rootPath);
  }

  @Override
  public boolean addUser(User user) {
    var userDir = getUserDirectory(user.getEmail());
    if (userDir.exists()) {
      return false;
    }
    userDir.mkdir();
    var userFile = getUserFile(user.getEmail());
    return saveToFile(user, userFile);
  }

  @Override
  public User getUser(String email) {
    var file = getUserFile(email);
    return readFromJsonFile(file, User.class);
  }

  @Override
  public boolean addUrlAlias(UrlAlias urlAlias) {
    var userDir = getUserDirectory(urlAlias.getUser());
    if (!userDir.exists()) {
      throw new IllegalArgumentException(
        String.format("Cannot add alias, user '%s' was not created", urlAlias.getUser()));
    }
    var file = getAliasFile(urlAlias.getAlias(), urlAlias.getUser());
    return saveToFile(urlAlias, file);
  }

  @Override
  public UrlAlias getUrlAlias(String alias) {
    var file = getAliasFile(alias);
    if (file == null) {
      return null;
    }
    return readFromJsonFile(file, UrlAlias.class);
  }

  @Override
  public boolean deleteUrlAlias(String alias) {
    var file = getAliasFile(alias);
    if (file == null) {
      return false;
    }
    file.delete();
    return true;
  }

  @Override
  public List<UrlAlias> getUserAliases(String user) {
    var userDir = getUserDirectory(user);
    var userFiles = userDir.listFiles(new FilenameFilter() {
      @Override
      public boolean accept(File dir, String name) {
        return name.toLowerCase().endsWith(jsonFileExtension);
      }
    });
    var userUrls = new ArrayList<UrlAlias>();
    if (userFiles != null) {
      for (File file : userFiles) {
        var urlAlias = readFromJsonFile(file, UrlAlias.class);
        userUrls.add(urlAlias);
      }
    }

    return userUrls;
  }

  @Override
  public void clear() {
    var rootDir = new File(rootPath);
    var userDirs = rootDir.listFiles();
    if (userDirs != null) {
      for (var userDir : userDirs) {
        clearDirectory(userDir);
        userDir.delete();
      }
    }
  }

  private void clearDirectory(File dir) {
    File[] allContents = dir.listFiles();
    if (allContents != null) {
      for (File file : allContents) {
        file.delete();
      }
    }
  }

  private void createDirectory(String path) {
    var dir = new File(path);
    if (!dir.exists()) {
      dir.mkdir();
    }
  }

  private boolean saveToFile(Object src, File dest) {
    var g = new Gson();
    try {
      if (dest.createNewFile()) {
        var writer = new FileWriter(dest);
        writer.write(g.toJson(src));
        writer.flush();
        writer.close();
        return true;
      } else {
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

    return false;
  }

  public <T> T readFromJsonFile(File src, Class<T> classOfT) {
    if (src.exists()) {
      try {
        var str = new String(Files.readAllBytes(src.toPath()));
        var g = new Gson();
        return g.fromJson(str, classOfT);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    return null;
  }

  private File getUserDirectory(String email) {
    return new File(String.join("/", rootPath, email));
  }

  private File getUserFile(String email) {
    return new File(String.join("/", rootPath, email, email + userFileExtension));
  }

  private File getAliasFile(String alias, String user) {
    return new File(String.join("/", rootPath, user, alias + jsonFileExtension));
  }

  private File getAliasFile(String alias) {
    var rootDir = new File(rootPath);
    var users = rootDir.list();
    if (users != null) {
      for (var user : users) {
        var aliasFile = getAliasFile(alias, user);
        if (aliasFile.exists()) {
          return aliasFile;
        }
      }
    }
    return null;
  }
}
