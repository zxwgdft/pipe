package com.tonto.framework.mybatis.generate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.type.JdbcType;
import org.apache.log4j.Logger;

import com.tonto.data.database.DataBaseType;
import com.tonto.data.database.DataTypeUtil;
import com.tonto.data.database.model.Column;
import com.tonto.data.database.model.DataBase;
import com.tonto.data.database.model.Table;
import com.tonto.data.database.model.constraint.ColumnConstraint;
import com.tonto.framework.mybatis.JdbcTypeUtil;
import com.tonto.framework.mybatis.core.generate.annotation.DataColumn;
import com.tonto.framework.mybatis.core.generate.annotation.DataReference;
import com.tonto.framework.mybatis.core.generate.annotation.DataTable;
import com.tonto.utils.reflect.NameUtil;
import com.tonto.utils.reflect.ReflectUtil;

/**
 * 数据模型对象生成器
 * 
 * @author TontoZhou
 * 
 */
public class DataModelGenerator {

	private final static Logger logger = Logger.getLogger(DataModelGenerator.class);

	private ModelNameConverter modelNameConverter;

	public void generate(DataBase dataBase, String basePackage, DataBaseType dbType, String path) {

		List<ModelClass> modelClasses = new ArrayList<>();
		ModelNameConverter converter = modelNameConverter == null ? defaultNameConverter : modelNameConverter;

		for (Table table : dataBase.getChildren()) {

			ModelClass modelClass = new ModelClass();
			modelClass.classPackage = converter.convertPackage(table, basePackage);
			modelClass.className = converter.convertTableName(table);
			modelClass.tableName = table.getName();

			for (Column column : table.getChildren()) {
				ModelProperty property = new ModelProperty();
				property.columnName = column.getName();
				property.propertyName = converter.convertColumnName(column);
				property.propertyClass = DataTypeUtil.getJavaType(column, dbType);
				property.primary = column.isPrimary();
				
				
				if(dbType == DataBaseType.ORACLE && Number.class.isAssignableFrom(property.propertyClass))
					property.jdbcType = JdbcTypeUtil.getJdbcType(property.propertyClass);
				else
					property.jdbcType = JdbcTypeUtil.getJdbcType(dbType,column.getDataType());
				
				/*
				 * 外键，需要处理组合外键
				 */
				ColumnConstraint columnConstraint = column.getForeignKey();
				if (columnConstraint != null) {
					Reference reference = new Reference();
					reference.table = columnConstraint.getReferencedTable();
					reference.column = columnConstraint.getReferencedColumn();

					if (column.isMultiForeignKey())
						reference.id = columnConstraint.getTableConstraint().getName();

					property.reference = reference;
				}

				/*
				 * 唯一键
				 */
				columnConstraint = column.getForeignKey();
				if (columnConstraint != null) {
					property.unique = true;
					if (column.isMultiUnique())
						property.uniqueId = columnConstraint.getTableConstraint().getName();
				}

				modelClass.addProperty(property);
			}

			modelClasses.add(modelClass);
		}

		createClassFile(modelClasses, path);
	}

	private void createClassFile(List<ModelClass> modelClasses, String path) {

		for (ModelClass modelClass : modelClasses)
			createClassFile(modelClass, path);
	}

	private void createClassFile(ModelClass modelClass, String path) {
		String tab = "\t";

		StringBuilder sb = new StringBuilder();

		sb.append("package ").append(modelClass.classPackage).append(";\n\n");

		
		String[] classNames = new String[modelClass.importClassSet.size()];
	
		
		int i = 0;
		for (Class<?> importClass : modelClass.importClassSet)
			classNames[i++] = importClass.getName();
			
		Arrays.sort(classNames);

		for (String className : classNames)		
		{
			if(!className.matches("^java\\.lang\\.\\w+$"))
				sb.append("import ").append(className).append(";\n");
		}
		
		sb.append("\n@DataTable(\"").append(modelClass.tableName).append("\")\n");
		sb.append("public class ").append(modelClass.className).append(" {\n\n");

		for (ModelProperty property : modelClass.properties)
			sb.append(tab).append("private ").append(property.propertyClass.getSimpleName()).append(" ").append(property.propertyName)
					.append(";\n\n");

		for (ModelProperty property : modelClass.properties) {
			// 注解DataColumn
			sb.append(tab).append("@DataColumn(name = \"").append(property.columnName).append("\", jdbcType = JdbcType.").append(property.jdbcType);
			if (property.primary)
				sb.append(", primary = true");
			if (property.unique)
				sb.append(", unique = true");
			if (property.uniqueId != null)
				sb.append(", uniqueId = \"").append(property.uniqueId).append("\"");
			sb.append(")\n");

			// 注解DataReference
			if (property.reference != null) {
				Reference reference = property.reference;
				sb.append(tab).append("@DataReference(table = \"").append(reference.table).append("\", column = \"")
						.append(reference.column).append("\"");
				if (reference.id != null)
					sb.append(", id = \"").append(reference.id).append("\"");
				sb.append(")\n");
			}

			// getMethod
			sb.append(tab).append("public ").append(property.propertyClass.getSimpleName()).append(" ")
					.append(NameUtil.addGet(property.propertyName)).append("() {\n").append(tab).append(tab).append("return ")
					.append(property.propertyName).append(";\n").append(tab).append("}\n\n");

			// setMethod
			sb.append(tab).append("public void ").append(NameUtil.addSet(property.propertyName)).append("(")
					.append(property.propertyClass.getSimpleName()).append(" ").append(property.propertyName).append(") {\n")
					.append(tab).append(tab).append("this.").append(property.propertyName).append(" = ").append(property.propertyName)
					.append(";\n").append(tab).append("}\n\n");
		}

		sb.append("}");

		try {
			Files.write(getFilePath(path, modelClass.classPackage, modelClass.className), sb.toString().getBytes());
			logger.info("---创建表[" + modelClass.tableName + "]对应的数据模型Java类[" + modelClass.className + "]成功---");
		} catch (IOException e) {
			logger.warn("无法创建表[" + modelClass.tableName + "]对应的数据模型Java类[" + modelClass.className + "]", e);
		}
	}

	private static Path getFilePath(String basePath, String _package, String name) throws IOException {
		String[] more = null;
		name += ".java";
		if (_package == null || _package.equals("")) {
			more = new String[] { name };
		} else {
			more = _package.split("\\.");
			Files.createDirectories(Paths.get(basePath, more));
			more = Arrays.copyOf(more, more.length + 1);
			more[more.length - 1] = name;
		}

		Path path = Paths.get(basePath, more);
		return path;
	}

	private static class ModelClass {

		Set<Class<?>> importClassSet = new HashSet<>();
		String classPackage;
		String className;
		String tableName;

		List<ModelProperty> properties = new ArrayList<>();

		public ModelClass() {
			importClassSet.add(DataTable.class);
			importClassSet.add(DataColumn.class);
			importClassSet.add(JdbcType.class);
		}

		public void addProperty(ModelProperty property) {
			Class<?> clazz = property.propertyClass;

			if (clazz.isArray())
				clazz = ReflectUtil.getArrayType(clazz);

			if (!clazz.isPrimitive() && !clazz.getName().matches("^java.lang.[^.]"))
				importClassSet.add(clazz);

			properties.add(property);

			if (property.reference != null)
				importClassSet.add(DataReference.class);

		}

	}

	private static class ModelProperty {

		Class<?> propertyClass;
		String columnName;
		String propertyName;
		Reference reference;
		boolean primary;
		boolean unique;
		String uniqueId;
		JdbcType jdbcType;
	}

	private static class Reference {

		String id;
		String table;
		String column;

	}

	public static interface ModelNameConverter {

		public String convertColumnName(Column column);

		public String convertTableName(Table table);

		public String convertPackage(Table table, String basePackage);
	}

	private static ModelNameConverter defaultNameConverter = new ModelNameConverter() {

		@Override
		public String convertColumnName(Column column) {
			return NameUtil.underline2hump(column.getName());
		}

		@Override
		public String convertTableName(Table table) {
			return NameUtil.firstUpperCase(NameUtil.underline2hump(table.getName()));
		}

		@Override
		public String convertPackage(Table table, String basePackage) {
			return basePackage;
		}

	};
}
