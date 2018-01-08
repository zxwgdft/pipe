package com.tonto.framework.mybatis.generate;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.tonto.data.database.CommonDataBase;
import com.tonto.data.database.DataBaseConfig;
import com.tonto.data.database.DataBaseType;
import com.tonto.data.database.model.DataBase;

public class BaseGenerateUtil {

	public static void generateMode(DataBaseConfig config, DataBaseType type, String path, String packageName) {

		CommonDataBase druidDataBase = new CommonDataBase(config) {

			@Override
			protected DataSource createRealDataSource() {
				DataBaseType type = config.getType();

				if (type == null)
					throw new NullPointerException("Database Type Can't Be Null");

				DruidDataSource dataSource = new DruidDataSource();
				dataSource.setUrl(config.getUrl());
				dataSource.setPassword(config.getPassword());
				dataSource.setUsername(config.getUsername());
				dataSource.setName(config.getName());
				dataSource.setMaxWait(10000);

				return dataSource;
			}

			@Override
			protected boolean initialize() {
				try {
					((DruidDataSource) realDataSource).init();
				} catch (SQLException e) {
					throw new RuntimeException("数据源初始化异常", e);
				}
				return true;
			}

			@Override
			protected boolean destroy() {
				((DruidDataSource) realDataSource).close();
				return true;
			}

		};

		DataBase dataBase = druidDataBase.getDataBase(true);

		DataModelGenerator generator = new DataModelGenerator();
		// String path = System.getProperty("user.dir")+"\\src\\main\\java";
		generator.generate(dataBase, packageName, type, path);

	}

}
