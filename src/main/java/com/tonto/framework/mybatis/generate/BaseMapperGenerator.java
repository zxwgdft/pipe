package com.tonto.framework.mybatis.generate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.tonto.data.database.DataBaseType;
import com.tonto.data.database.model.Column;
import com.tonto.data.database.model.Table;
import com.tonto.framework.mybatis.JdbcTypeUtil;
import com.tonto.utils.reflect.NameUtil;
import com.tonto.utils.template.ObjectMessageTemplate;

public class BaseMapperGenerator {

	private static ObjectMessageTemplate template;

	static {
		try {
			template = new ObjectMessageTemplate(BaseMapperGenerator.class.getResourceAsStream("base_sql_mapper.xml"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String generateSqlMapper(Table table, DataBaseType type, String model) {

		String tablename = table.getName();
		String r1 = "\n\t\t";
		String r2 = "\n\t\t\t";

		String where = r1 + "where ";
		boolean onlyOnePrimary = true;

		StringBuilder select = new StringBuilder();
		StringBuilder insert1 = new StringBuilder();
		StringBuilder insert2 = new StringBuilder();

		StringBuilder update = new StringBuilder();
		update.append("update ").append(tablename).append(" set ");

		for (Column column : table.getChildren()) {
			String a = column.getName();
			String b = NameUtil.underline2hump(a);
			String c = "#{" + b + ",jdbcType=" + JdbcTypeUtil.getJdbcType(type, column.getDataType()) + "}";

			if (a.equals(b)) {
				select.append(r2).append(a).append(",");
			} else {
				select.append(r2).append(a).append(" as ").append(b).append(",");
			}

			insert1.append(r2).append(a).append(",");
			insert2.append(r2).append(c).append(",");

			update.append(r2).append(a).append(" = ").append(c).append(",");

			if (column.isPrimary()) {
				if (onlyOnePrimary) {
					where += a + " = " + c;
					onlyOnePrimary = false;
				} else {
					where += r1 + "and " + a + " = " + c;
				}

			}
		}

		select.deleteCharAt(select.length() - 1);
		insert1.deleteCharAt(insert1.length() - 1);
		insert2.deleteCharAt(insert2.length() - 1);

		String selectStr = select.toString();

		String findSql = "select" + selectStr + r1 + "from " + tablename;
		String getSql = findSql + where;
		String deleteSql = "delete from " + tablename + where;
		String insertSql = "insert into " + tablename + r1 + " (" + insert1.toString() + r1 + ")values" + r1 + "(" + insert2.toString()
				+ r1 + ")";
		String updateSql = update.append(where).toString();

		Map<String, String> data = new HashMap<>();
		data.put("findSql", findSql);
		data.put("deleteSql", deleteSql);
		data.put("getSql", getSql);
		data.put("insertSql", insertSql);
		data.put("updateSql", updateSql);
		data.put("model", model);

		return template.createMessage(data);
	}

}
