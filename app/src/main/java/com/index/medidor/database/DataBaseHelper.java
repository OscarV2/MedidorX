package com.index.medidor.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.index.medidor.R;
import com.index.medidor.model.Estaciones;
import com.index.medidor.model.MarcaCarros;
import com.index.medidor.model.ModeloCarros;
import com.index.medidor.model.Recorridos;
import com.index.medidor.model.Tanqueadas;
import com.index.medidor.model.Usuario;
import com.index.medidor.model.UsuarioHasModeloCarro;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DataBaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "medidor.db";
    private static final int VERSION = 1;

    private Dao<Usuario, Integer> daoUsuario = null;
    private RuntimeExceptionDao<Usuario, Integer> usuarioRuntimeDao = null;

    private Dao<Tanqueadas, Integer> daoTanqueadas = null;
    private RuntimeExceptionDao<Tanqueadas, Integer> tanqueadasRuntimeDao = null;

    private Dao<Estaciones, Integer> daoEstaciones = null;
    private RuntimeExceptionDao<Estaciones, Integer> estacionesRuntimeDao = null;

    private Dao<Recorridos, Integer> daoRecorridos = null;
    private RuntimeExceptionDao<Recorridos, Integer> recorridosRuntimeDao = null;

    private Dao<MarcaCarros, Integer> daoMarcas = null;
    private RuntimeExceptionDao<MarcaCarros, Integer> marcasRuntimeDao = null;

    private Dao<ModeloCarros, Integer> daoModelos = null;
    private RuntimeExceptionDao<ModeloCarros, Integer> modelosRuntimeDao = null;

    private Dao<UsuarioHasModeloCarro, Integer> daoUsuarioHasModeloCarros = null;
    private RuntimeExceptionDao<UsuarioHasModeloCarro, Integer> usuarioHasModeloCarroRuntimeDao = null;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION, R.raw.medidor_config);
 //       super(context, DATABASE_NAME, null, VERSION, 0);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {

        try {
            TableUtils.createTable(connectionSource, Usuario.class);
            TableUtils.createTable(connectionSource, Estaciones.class);
            TableUtils.createTable(connectionSource, Tanqueadas.class);
            TableUtils.createTable(connectionSource, Recorridos.class);
            TableUtils.createTable(connectionSource, MarcaCarros.class);
            TableUtils.createTable(connectionSource, ModeloCarros.class);
            TableUtils.createTable(connectionSource, UsuarioHasModeloCarro.class);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connection, int oldVersion, int newVersion) {

        try {
            TableUtils.dropTable(connection, Usuario.class, true);
            TableUtils.dropTable(connection, Estaciones.class, true);
            TableUtils.dropTable(connection, Tanqueadas.class, true);
            TableUtils.dropTable(connection, Recorridos.class, true);
            TableUtils.dropTable(connection, MarcaCarros.class,true);
            TableUtils.dropTable(connection, ModeloCarros.class,true);
            TableUtils.dropTable(connection, UsuarioHasModeloCarro.class,true);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public Dao<Usuario, Integer> getDaoUsuario() throws SQLException {
        if(daoUsuario == null) daoUsuario = getDao(Usuario.class);
        return daoUsuario;
    }

    public RuntimeExceptionDao<Usuario, Integer> getUsuarioRuntimeDao() {
        if(usuarioRuntimeDao == null) usuarioRuntimeDao = getRuntimeExceptionDao(Usuario.class);
        return usuarioRuntimeDao;
    }

    public Dao<Tanqueadas, Integer> getDaoTanqueadas() throws SQLException {
        if(daoTanqueadas == null) daoTanqueadas = getDao(Tanqueadas.class);
        return daoTanqueadas;
    }

    public RuntimeExceptionDao<Tanqueadas, Integer> getTanqueadasRuntimeDao() {
        if(tanqueadasRuntimeDao == null) tanqueadasRuntimeDao = getRuntimeExceptionDao(Tanqueadas.class);
        return tanqueadasRuntimeDao;
    }

    public Dao<Estaciones, Integer> getDaoEstaciones() throws SQLException {
        if(daoEstaciones == null) daoEstaciones = getDao(Estaciones.class);
        return daoEstaciones;
    }


    public RuntimeExceptionDao<Estaciones, Integer> getEstacionesRuntimeDao() {
        if(estacionesRuntimeDao == null) estacionesRuntimeDao = getRuntimeExceptionDao(Estaciones.class);
        return estacionesRuntimeDao;
    }

    public Dao<Recorridos, Integer> getDaoRecorridos() throws SQLException {
        if (daoRecorridos == null) daoRecorridos = getDao(Recorridos.class);
        return daoRecorridos;
    }


    public RuntimeExceptionDao<Recorridos, Integer> getRecorridosRuntimeDao() {
        if(recorridosRuntimeDao == null) recorridosRuntimeDao = getRuntimeExceptionDao(Recorridos.class);
        return recorridosRuntimeDao;
    }

    public Dao<MarcaCarros, Integer> getDaoMarcas() throws SQLException {
        if (daoMarcas == null) daoMarcas = getDao(MarcaCarros.class);
        return daoMarcas;
    }

    public RuntimeExceptionDao<MarcaCarros, Integer> getMarcasRuntimeDao() {
        if(marcasRuntimeDao == null) marcasRuntimeDao = getRuntimeExceptionDao(MarcaCarros.class);

        return marcasRuntimeDao;
    }

    public Dao<ModeloCarros, Integer> getDaoModelos() throws SQLException {
        if (daoModelos == null) daoModelos = getDao(ModeloCarros.class);
        return daoModelos;
    }

    public RuntimeExceptionDao<ModeloCarros, Integer> getUsua() {
        if(modelosRuntimeDao == null) modelosRuntimeDao = getRuntimeExceptionDao(ModeloCarros.class);
        return modelosRuntimeDao;
    }

    public Dao<UsuarioHasModeloCarro, Integer> getDaoUsuarioHasModeloCarros() throws SQLException {
        if (daoUsuarioHasModeloCarros == null) daoUsuarioHasModeloCarros = getDao(UsuarioHasModeloCarro.class);
        return daoUsuarioHasModeloCarros;
    }

    public RuntimeExceptionDao<UsuarioHasModeloCarro, Integer> getUsuarioHasModeloCarroIntegerRuntimeDao() {
        if(usuarioHasModeloCarroRuntimeDao == null) usuarioHasModeloCarroRuntimeDao = getRuntimeExceptionDao(UsuarioHasModeloCarro.class);
        return usuarioHasModeloCarroRuntimeDao;
    }

    @Override
    public void close() {
        super.close();
        daoUsuario = null;
        daoEstaciones = null;
        daoTanqueadas = null;
        daoRecorridos = null;
        daoMarcas = null;
        daoModelos = null;
        daoUsuarioHasModeloCarros = null;
        tanqueadasRuntimeDao = null;
        estacionesRuntimeDao = null;
        recorridosRuntimeDao = null;
        usuarioRuntimeDao = null;
        marcasRuntimeDao = null;
        modelosRuntimeDao = null;
        usuarioHasModeloCarroRuntimeDao = null;
    }
}
