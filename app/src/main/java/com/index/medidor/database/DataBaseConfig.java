package com.index.medidor.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.IOException;
import java.sql.SQLException;

public class DataBaseConfig extends OrmLiteConfigUtil {

    public static void main(String[] args) throws IOException, SQLException {

        writeConfigFile("medidor_config.txt");

    }
}
