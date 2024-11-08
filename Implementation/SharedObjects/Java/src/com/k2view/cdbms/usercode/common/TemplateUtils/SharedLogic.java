/////////////////////////////////////////////////////////////////////////
// Project Shared Functions
/////////////////////////////////////////////////////////////////////////

package com.k2view.cdbms.usercode.common.TemplateUtils;

import java.util.*;
import java.sql.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.kafka.common.protocol.types.Field.Bool;

import com.github.jknack.handlebars.*;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.k2view.cdbms.shared.*;
import com.k2view.cdbms.lut.*;
import com.k2view.cdbms.shared.utils.UserCodeDescribe.*;
//import com.k2view.cdbms.usercode.*;
import com.k2view.fabric.common.Util;

import static com.k2view.cdbms.shared.user.UserCode.*;
import static com.k2view.cdbms.usercode.common.SharedGlobals.*;
import java.math.*;
import java.io.*;
import com.k2view.cdbms.shared.user.UserCode;
import com.k2view.cdbms.sync.*;
import com.k2view.cdbms.shared.logging.LogEntry.*;
import com.k2view.cdbms.func.oracle.OracleToDate;
import com.k2view.cdbms.func.oracle.OracleRownum;
import com.k2view.fabric.events.*;
import com.k2view.fabric.fabricdb.datachange.TableDataChange;
import static com.k2view.cdbms.shared.user.ProductFunctions.*;
import static com.k2view.cdbms.shared.utils.UserCodeDescribe.FunctionType.*;

@SuppressWarnings({"DefaultAnnotationParam", "unchecked"})
public class SharedLogic {


	@out(name = "result", type = String.class, desc = "")
	public static String transform(String templateContent, Object data) throws Exception {
		Handlebars handlebars = new Handlebars();
		
		handlebars.registerHelper("indexPlus", new Helper<Integer>() {
			public Integer apply(Integer index, Options options) {
				return index + 1;
			}
		});
		
        handlebars.registerHelper("indexPlusLength", new Helper<Integer>() {
			public Integer apply(Integer index, Options options) {
				return index + Integer.parseInt(options.param(0).toString());
			} 
		});
		      handlebars.registerHelper("increase", new Helper<Integer>() {
			public Integer apply(Integer number, Options options) {
				return number + (Integer)options.hash.get("inc");
			}
		});
		
		handlebars.registerHelper("getTableName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("TARGET_TABLE_NAME");
			}
		});
		
		handlebars.registerHelper("getFieldName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				//log.info(map.get("TARGET_FIELD_NAME"));
				return map.get("TARGET_FIELD_NAME");
			}
		});
		
		      handlebars.registerHelper("getFabricFieldName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				//log.info(map.get("TARGET_FIELD_NAME"));
				return map.get("FABRIC_FIELD_NAME");
			}
		});
		
		handlebars.registerHelper("getLUFieldName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				//log.info(map.get("FIELD_NAME"));
				return map.get("FIELD_NAME");
			}
		});
		
		handlebars.registerHelper("getGDActorName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
		              String actorName = map.get("ACTOR");
				int actorNameEnd = actorName.lastIndexOf(".actor");
		              if (actorNameEnd > 0) {
		                  actorName = actorName.substring(0, actorNameEnd);
		              }
				return actorName;
			}
		});
		handlebars.registerHelper("getSourceTableName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("FABRIC_TABLE_NAME");
			}
		});
		
		handlebars.registerHelper("getSourceFieldName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return (map.get("FABRIC_FIELD_NAME")).toUpperCase();
			}
		});
		
		handlebars.registerHelper("getSequenceName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
		              String seqeunceName = map.get("SEQUENCE_NAME");
		              try {
		                  Object generateFlow = fabric().fetch("set GENERATE_FLOW").firstValue();
		                  if (generateFlow != null && "true".equalsIgnoreCase(generateFlow.toString())) {
		                      seqeunceName = "Gen_" + seqeunceName;
		                  }
		
		              } catch (Exception e) {
		                  log.error("Failed to get Sequence Name");
		              }
				return seqeunceName;
			}
		});
		
		handlebars.registerHelper("getSequenceActorName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("SEQUENCE_NAME") + "_Actor";
			}
		});
		
		handlebars.registerHelper("getSeqCacheInterface", new Helper<String>() {
			public String apply(String luName, Options options) {
                return getGlobal("SEQ_CACHE_INTERFACE", "TDM");
			}
		});
		
		handlebars.registerHelper("if_even", new Helper<Integer>(){
			public Boolean apply(Integer index, Options options) {
				return ((index % 2) == 0);
			}
		});
		
		handlebars.registerHelper("getParentFieldName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("PARENT_FIELD_NAME");
			}
		});
		
		handlebars.registerHelper("getChildFieldName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("FIELD_NAME").toUpperCase();
			}
		});
		
		handlebars.registerHelper("getDeleleColumnName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("name");
			}
		});
		
		handlebars.registerHelper("getDeleleColumnType", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("datatype");
			}
		});
		
		handlebars.registerHelper("getInputFieldName", new Helper<Map<String, String>>() {
			public String apply(Map<String, String> map, Options options) {
				return map.get("FIELD_NAME");
			}
		});
		
		handlebars.registerHelper("eq", ConditionalHelpers.eq);
		handlebars.registerHelper("neq", ConditionalHelpers.neq);
        handlebars.registerHelper("or", ConditionalHelpers.or);
		
		Template template = handlebars.compileInline(templateContent);
		
		return template.apply(data);
	}

	@out(name = "res", type = Object.class, desc = "")
	public static Object buildTemplateData(String luName, String luTable, String targetDbInterface, String targetDbSchema, 
        String targetDbTable, String tableIidFieldName, String sequenceName, String flowType) throws Exception {

            
		//String luName = getLuType().luName;
		
		if (luName == null || Util.isEmpty(luName)) {
			luName = getLuType().luName;
		}
		
		
		List<String> luTableColumns = getLuTableColumns(luName, luTable);
		Object[] targetTableData = getDbTableColumns(targetDbInterface, targetDbSchema, targetDbTable);
		String seqIID;
		String seqName;
		
		
		luTableColumns.replaceAll(String::toUpperCase);
		
		Map<String, Object> map = new TreeMap<>();
		map.put("LU_NAME", luName);
		map.put("LU_TABLE", luTable);
		map.put("DELETE_TABLE", TDM_DELETE_TABLES_PREFIX + luTable);
		map.put("LU_TABLE_COLUMNS", luTableColumns);
		map.put("TARGET_INTERFACE", targetDbInterface);
		map.put("TARGET_SCHEMA", targetDbSchema);
		map.put("TARGET_TABLE", targetDbTable);
		
        List <String> linkFields = (ArrayList<String>) targetTableData[1];

        if ("DELETE".equals(flowType)) {
            //List<String> luTarTableColumns = getLuTableColumns(luName, (TDM_DELETE_TABLES_PREFIX + luTable));
            //luTarTableColumns.replaceAll(String::toUpperCase);
            //linkFields = luTarTableColumns;

            Set<Map<String,String>> luTarTableColsSet = getDeleteTablesFields(luName, targetDbInterface, targetDbSchema, targetDbTable);
            List<String> luTarTableColumns = new ArrayList<>();
            for(Map<String, String> fieldMap : luTarTableColsSet) {
                luTarTableColumns.add(fieldMap.get("FIELD_NAME"));
            }
            linkFields = luTarTableColumns;
        } else if (linkFields == null || linkFields.size() == 0) {

            Set<Map<String,String>> argsFields = getPopArgumentListForDelete(luName, luTable);
            for (Map<String,String> rec : argsFields) {
                if ("".equals(rec.get("PARENT_TABLE"))) {
                    linkFields.add(rec.get("FIELD_NAME"));
                }
            }
        }
        if ("DELETE".equals(flowType)) {
            map.put("TARGET_TABLE_COLUMNS", linkFields);
        } else {
            map.put("TARGET_TABLE_COLUMNS", targetTableData[0]);
        }
        map.put("TARGET_TABLE_PKS", linkFields);
		
		Object mainTableName = fabric().fetch("SET " + luName + ".ROOT_TABLE_NAME").firstValue();
		
		List<String> mainTables = new ArrayList<>();
		if (mainTableName != null) {
			mainTables = Arrays.asList(mainTableName.toString().split(","));
		}
		String mainTable = "false";
		//log.info("LU_NAME:" + luName + ", mainTables: " + mainTables + ", mainTableName: " + mainTableName);
		if (mainTables.contains(luTable)) {
			mainTable = "true";
		}
		if ("".equals(tableIidFieldName)) {
			seqIID = "NO_ID";
			seqName = "";
		} else {
			seqIID = tableIidFieldName;
			seqName = sequenceName;
		}
		map.put("MAIN_TABLE_SEQ_ID", seqIID);
		map.put("MAIN_TABLE_SEQ_NAME", seqName);
		//log.info("buildTemplateData - LU_TABLE: " + luTable + ", MAIN_TABLE_SEQ_ID: " + seqIID);
		String cmd = "broadway " + luName + ".getTableSequenceMapping LU_NAME=" + luName + ", FABRIC_TABLE_NAME = '" + luTable + "', RESULT_STRUCTURE=ROW";
		//log.info("buildTemplateData - cmd: " + cmd);
		
		ArrayList<Object> tableSeq = (ArrayList<Object>)fabric().fetch(cmd).firstRow().get("value");
		//log.info("buildTemplateData - tableSeq: " + tableSeq);
		
		if (tableSeq != null) {
			Object[] tableSeqArr = tableSeq.toArray(new Object[tableSeq.size()]);
			if (tableSeqArr.length > 0) {
				map.put("TABLE_SEQ_DATA", tableSeqArr);
			} else {
				map.put("TABLE_SEQ_DATA", null);
			} 
		} else {
			map.put("TABLE_SEQ_DATA", null);
		}
		map.put("MAIN_TABLE_IND", mainTable);
		//log.info("MAIN_TABLE_IND: " + mainTable + ", table: " + luTable);
		
		return map;
	}

	@out(name = "res", type = List.class, desc = "")
	public static List<String> getLuTableColumns(String luName, String table) throws Exception {
		List<String> al = null;// = new ArrayList<>();
		LUType luType = null;
		if (luName == null || Util.isEmpty(luName)) {
			luType = getLuType();
		} else {
			luType = LUType.getTypeByName(luName);
		}
		if(luType == null || !luType.ludbObjects.containsKey(table)) 
			return al;
			
		al = new ArrayList<>(luType.ludbObjects.get(table).getLudbObjectColumns().keySet());
		al.replaceAll(String::toLowerCase);
		return al;
	}

	@out(name = "res", type = List.class, desc = "")
	public static List<String> getLuTables(String luName) throws Exception {
		List<String> al = new ArrayList<>();
		LUType luType = null;
		if (luName == null || Util.isEmpty(luName)) {
			luType = getLuType();
		} else {
			luType = LUType.getTypeByName(luName);
		}
		
		if(luType == null)
			return al;
		LUType finalLuType = luType;
		luType.ludbTables.forEach((s, s2) -> {
			Db.Rows checkTable = null;
			try {
				checkTable = fabric().fetch("broadway " + finalLuType.luName + ".filterOutTDMTables tableName='" +
						s + "', luName=" + finalLuType.luName + ", RESULT_STRUCTURE=ROW");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		
			if (checkTable != null && checkTable.firstValue() != null) {
				al.add(s);
			}
			if (checkTable != null) {
				checkTable.close();
			}
		});
		
		
		return al;
	}

	@out(name = "res", type = List.class, desc = "")
	public static List<Map<String,String>> getTablesForGenerate(String luName, String sourceInterface, String sourceSchema) throws Exception {
		List<Map<String,String>> result = new ArrayList<>();
		LUType luType = null;
		if (luName == null || Util.isEmpty(luName)) {
			luType = getLuType();
		} else {
			luType = LUType.getTypeByName(luName);
		}
        
        List<String> sourceTables = getDbTables(sourceInterface, sourceSchema);

		if(luType == null)
			return result;
		LUType finalLuType = luType;
		luType.ludbTables.forEach((s, s2) -> {
			Db.Rows checkTable = null;
            Map<String, String> map = new HashMap<>();
			try {
                
				checkTable = fabric().fetch("broadway " + finalLuType.luName + ".filterOutGenertors tableName='" +
						s + "', luName=" + finalLuType.luName + ", RESULT_STRUCTURE=ROW");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (checkTable != null && checkTable.firstValue() != null) {
                for (String sourceTable : sourceTables) {
                    if (sourceTable.equalsIgnoreCase(s)) {
                        map.put("luTable", s);
                        map.put("sourceTable", sourceTable);
                        result.add(map);
                        break;
                    }
                }
			}
			if (checkTable != null) {
				checkTable.close();
			}
		});


		return result;
	}

	@out(name = "res", type = Object.class, desc = "")
	public static Object getLuTablesMappedByOrder(String luName, Boolean reverseInd) throws Exception {
		List<List<String>> buckets = new ArrayList<>();
		LUType luType = null;
		if (luName == null || Util.isEmpty(luName)) {
			luType = getLuType();
		} else {
			luType = LUType.getTypeByName(luName);
		}
		
		if(luType == null)
			return "";
		
		List<TablePopulation> populations = luType.getPopulationCollection();
		// populations already ordered
		Set<String> tables = new HashSet<>();
		List<String> tmpBucket = new ArrayList<>();
		int currentOrder = 0;
		for (TablePopulation p : populations) {
			if (p.gettablePopulationOrder() > currentOrder) {
				if (!tmpBucket.isEmpty()) {
					buckets.add(tmpBucket);
					tmpBucket = new ArrayList<>();
				}
				currentOrder = p.gettablePopulationOrder();
			}
		
			//The table name in TablePopulation is kept in Upper case, to get the original name, loop over luType.ludbTables
			String originalTableName = p.getLudbObjectName();
			for (String tableName : luType.ludbTables.keySet()) {
				if (tableName.equalsIgnoreCase(p.getLudbObjectName())) {
					originalTableName = tableName;
					break;
				}
			}
			Db.Rows checkTable = fabric().fetch("broadway " + luType.luName + ".filterOutTDMTables tableName='" +
			    originalTableName + "', luName=" + luType.luName + ", RESULT_STRUCTURE=ROW");
			
			String tableFiltered = "";
			if (checkTable != null && checkTable.firstValue() != null) {
				tableFiltered = "" + checkTable.firstValue();
			}
			
			if (checkTable != null) {
				checkTable.close();
			}
			if( !tables.contains(originalTableName)  && !Util.isEmpty(tableFiltered)) {
				tmpBucket.add(originalTableName);
				tables.add(originalTableName);
			}
		}
		// The last bucket
		if (!tmpBucket.isEmpty()) {
			buckets.add(tmpBucket);
		}
		if (reverseInd) {
			Collections.reverse(buckets);
		}
		
		return buckets;
		//return Json.get().toJson(buckets);
	}


public static String[] getDBCollection(DatabaseMetaData md, String catalogSchema) throws Exception {
	String catalog = null;
	String schema = null;
	
	
	ResultSet schemas = md.getSchemas();
	while (schemas.next()) {
		//log.info("getDBCollection - Schema: " + schemas.getString("TABLE_SCHEM"));
		if (catalogSchema.equalsIgnoreCase(schemas.getString("TABLE_SCHEM"))) {
			 schema = schemas.getString("TABLE_SCHEM");
			 break;
		}
	}
	if (schema == null) {
		ResultSet catalogs = md.getCatalogs();
		while (catalogs.next()) {
			//log.info("getDBCollection - Catalog: " + catalogs.getString("TABLE_CAT"));
			if (catalogSchema.equalsIgnoreCase(catalogs.getString("TABLE_CAT"))) {
				catalog = catalogs.getString("TABLE_CAT");
				break;
			}
		}
	}
	
	return new String[]{catalog, schema};
	
}

	@out(name = "columns", type = Object[].class, desc = "")
	@out(name = "pks", type = Object[].class, desc = "")
	public static Object[] getDbTableColumns(String dbInterfaceName, String catalogSchema, String table) throws Exception {
		ResultSet rs = null;
		ResultSet rs1 = null;
		ResultSet rs2 = null;
		String[] types = {"TABLE"};
		String targetTableName = table;
		
		try {
			DatabaseMetaData md = getConnection(dbInterfaceName).getMetaData();
			
			String[] dbSchemaType = getDBCollection(md, catalogSchema);
			String catalog = dbSchemaType[0];
			String schema = dbSchemaType[1];
			//log.info("getDbTableColumns - Catalog: " + catalog + ", Schema: " + schema);
			rs = md.getTables(catalog, schema, "%", types);
			
			while (rs.next()) {
				if (table.equalsIgnoreCase(rs.getString(3))) {
					targetTableName = rs.getString(3);
					//log.info("getDbTableColumns - tableName: " + targetTableName);
					break;
				}
			}
						
			rs1 = md.getColumns(catalog, schema, targetTableName, null);
			List<String> al = new ArrayList<>();
			while (rs1.next()) {
				al.add(rs1.getString("COLUMN_NAME"));
			}
					
		
			// get PKs
			rs2 = md.getPrimaryKeys(catalog, schema, targetTableName);
			List<String> al2 = new ArrayList<>();
			while (rs2.next()) {
				al2.add(rs2.getString("COLUMN_NAME"));
			}
					 
			return new Object[]{al,al2};
		} finally {
			if (rs != null)
				rs.close();
		 	if (rs1 != null)
				rs1.close();
			if (rs2 != null)
				rs2.close();
		}
	}


	@out(name = "res", type = List.class, desc = "")
	public static List<String> getDbTables(String dbInterfaceName, String catalogSchema) throws Exception {
		ResultSet rs = null;
		List<String> al = new ArrayList<>();
		try {
			DatabaseMetaData md = getConnection(dbInterfaceName).getMetaData();
			String[] dbSchemaType = getDBCollection(md, catalogSchema);
			String catalog = dbSchemaType[0];
			String schema = dbSchemaType[1];
			rs = md.getTables(catalog, schema, "%", null);
			while (rs.next()) {
				al.add(rs.getString(3));
			}
		} finally {
			if (rs != null)
				rs.close();
		}
		return al;
	}


	@out(name = "res", type = Object.class, desc = "")
	public static Object buildSeqTemplateData(String seqName, String redisOrDBName, String initiationScriptOrValue) throws Exception {
		Map<String, Object> map = new TreeMap<>();
		map.put("SEQUENCE_NAME", seqName);
		map.put("SEQUENCE_REDIS_DB", redisOrDBName);
		map.put("INITIATE_VALUE_FLOW", initiationScriptOrValue);
		return map;
	}


	@out(name = "res", type = Object.class, desc = "")
	public static Object buildTransTemplateData(String transName, Object transColumns, Object transKeys) throws Exception {
		Map<String, Object> map = new TreeMap<>();
		map.put("TRANSLATION_NAME", transName);
		map.put("TRANS_COLUMNS", transColumns);
		map.put("TRANS_KEYS", transKeys);
		
		return map;
	}


	@out(name = "output", type = Map.class, desc = "")
	public static Map<String,Object> getTransDefaults(Object[] transDefinition, String luName, String transName) throws Exception {
		HashMap<String, Object> output = new HashMap<>();
		for (Object rec : transDefinition) {
			HashMap<String, String> map = (HashMap<String, String>) rec;
			if (transName.equals(map.get("translation_name")) && 
				("ALL".equals(map.get("owner_lu")) || luName.equals(map.get("owner_lu"))) ) {
					output.put(map.get("title"), map.get("default_value"));
					
			}
		}
		
		return output;
	}

    @out(name = "result", type = List.class, desc = "")
	public static List<HashMap<String, String>> getLuTableColumnsAndTypes(String luName, String table) throws Exception {
		LUType luType = null;
        List<HashMap<String, String>> tableData = new ArrayList<HashMap<String, String>>();
		if (luName == null || Util.isEmpty(luName)) {
			luType = getLuType();
		} else {
			luType = LUType.getTypeByName(luName);
		}
		if(luType == null || !luType.ludbObjects.containsKey(table)) 
			return tableData;
			
        HashMap<String, LudbColumn> m1 = new HashMap<>(luType.ludbObjects.get(table).getLudbObjectColumns());
        for (Map.Entry<String, LudbColumn> entry : m1.entrySet()) {
            HashMap<String, String> map = new HashMap<String, String>();
            String columnName = entry.getKey();
            LudbColumn columnData = entry.getValue();
            String columnType = columnData.columnType;
            map.put("columnName", columnName);
            map.put("columnType",columnType);
            
            tableData.add(map);
            
        }
		
		return tableData;
	}

	@out(name = "result", type = List.class, desc = "")
	public static List<Map<String,Object>> getPopulationsList(String luName) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		final LUType luType = LUType.getTypeByName(luName);

		luType.getPopulationCollection().forEach((tableEntry) -> {
		    //String table = tableEntry.getLudbObjectName();
		    String table = tableEntry.getTableObject().schemaAndTableName;
		    String popName =  tableEntry.getPopulationName();
			int tableOrder = tableEntry.gettablePopulationOrder();
		    
			//log.info("getPopulationsList - table: " + table + ", popName: " + popName);
			int flowNameEnd = popName.lastIndexOf(".flow");
			if (flowNameEnd > 0) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("tableName", table);
				map.put("populationName", popName.substring(0, flowNameEnd));
				map.put("tableOrder", tableOrder);
				result.add(map);
			} else {
				log.warn("Table " + table + " has a population that is not a Broadway Flow, No Generator will be created for such population");
			}
		});
		return result;
	}

	@out(name = "result", type = List.class, desc = "")
	public static List<Map<String,Object>> getPopulationsListForDelete(String luName) throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		final LUType luType = LUType.getTypeByName(luName);
		LinkedHashMap<String, Object> tablesMaps  = new LinkedHashMap<>();
		luType.getPopulationCollection().forEach((tableEntry) -> {
			//String table = tableEntry.getLudbObjectName();
			String table = tableEntry.getTableObject().schemaAndTableName;
			String popName =  tableEntry.getPopulationName();
			int tableOrder = tableEntry.gettablePopulationOrder();
			Db.Rows checkTable = null;
			try {
				checkTable = fabric().fetch("broadway " + luType.luName + ".filterOutTDMTables tableName='" +
						table + "', luName=" + luType.luName + ", RESULT_STRUCTURE=ROW");
			} catch (SQLException e) {
				e.printStackTrace();
			}

			if (checkTable != null && checkTable.firstValue() != null) {
				//log.info("getPopulationsListForDelete - table: " + table + ", popName: " + popName);
				int flowNameEnd = popName.lastIndexOf(".flow");
				if (flowNameEnd > 0) {
					Map<String, Object> map = new HashMap<String, Object>();
					map.put("tableName", table);
					map.put("populationName", popName.substring(0, flowNameEnd));
					map.put("tableOrder", tableOrder);
					if (!tablesMaps.containsKey(table)) {
						tablesMaps.put(table, map);
						result.add(map);
					}
				} else {
					log.warn("Table " + table + " has a population that is not a Broadway Flow, No Generator will be created for such population");
				}
			}
			if (checkTable != null) {
				checkTable.close();
			}
		});
		return result;
	}

	@out(name = "result", type = Integer.class, desc = "")
	public static Integer getMaxPopulationOrder(String luName) throws Exception {
		final LUType luType = LUType.getTypeByName(luName);
		AtomicInteger maxTableOrder = new AtomicInteger(0);
		luType.getPopulationCollection().forEach((tableEntry) -> {
			String tableFiltered = "";
			try (Db.Rows checkTable = fabric().fetch("broadway " + luType.luName + ".filterOutTDMTables tableName='" +
					tableEntry.getTableObject().schemaAndTableName + "', luName=" + luType.luName + ", RESULT_STRUCTURE=ROW")) {


				if (checkTable != null && checkTable.firstValue() != null) {
					tableFiltered = "" + checkTable.firstValue();
				}
			} catch (SQLException throwables) {
				throwables.printStackTrace();
			}
			if (!Util.isEmpty(tableFiltered)) {
				if (tableEntry.gettablePopulationOrder() > maxTableOrder.get()) {
					maxTableOrder.set(tableEntry.gettablePopulationOrder());
				}
			}
		});
		return maxTableOrder.intValue();
	}


	@out(name = "result", type = Set.class, desc = "")
	public static Set<Map<String,String>> getPopArgumentList(String luName, String tableName) throws Exception {
		Set<Map<String, String>> result = new HashSet<Map<String, String>>();
		LUType luType = LUType.getTypeByName(luName);
        Map<String, List<LudbRelationInfo>> rel = null;
        if (luType.getLudbOppositePhysicalRelations() != null) {
		    rel = luType.getLudbOppositePhysicalRelations().get(tableName);
        }
        
		if (rel != null) {
			for (Object key : rel.keySet()) {
			    for (LudbRelationInfo ri : (List<LudbRelationInfo>) rel.get(key)) {
			        Map<String, String> map = new HashMap<>();
		    	    map.put("PARENT_TABLE", key.toString());
		        	map.put("PARENT_FIELD_NAME", ri.to.get("column"));
			        map.put("FIELD_NAME", ri.to.get("column"));
		    	    result.add(map);
		    	}
			}
		} else {

            Map<String, String> map = new HashMap<>();
            //log.info("getPopArgumentListForDelete REL IS NULL");
            map.put("PARENT_TABLE", "FABRIC_TDM_ROOT");
            map.put("PARENT_FIELD_NAME", getGlobal("ROOT_COLUMN_NAME", luName));
            map.put("FIELD_NAME", getGlobal("ROOT_COLUMN_NAME", luName));
            result.add(map);
        }
		return result;
	}
	
	@out(name = "result", type = Set.class, desc = "")
	public static Set<Map<String,String>> getPopArgumentListForDelete(String luName, String tableName) throws Exception {
       
		Set<Map<String, String>> result = new HashSet<Map<String, String>>();
		LUType luType = LUType.getTypeByName(luName);
        Map<String, List<LudbRelationInfo>> rel = null;
        if (luType.getLudbOppositePhysicalRelations() != null) {
		    rel = luType.getLudbOppositePhysicalRelations().get(tableName);
        }

		if (rel != null) {
            //log.info("REL IS NOT NULL, Handling table: <" + tableName + ">" + ", size of rel: " + rel.size() + ", keySet size: " + rel.keySet().size());
			for (Object key : rel.keySet()) {
			    for (LudbRelationInfo ri : (List<LudbRelationInfo>) rel.get(key)) {
			        //log.info("getPopArgumentListForDelete handling key:"  + key);
			        Map<String, String> map = new HashMap<>();
					String parentTable = key.toString();
                    //log.info("getPopArgumentListForDelete handling parentTable:"  + parentTable);
                    if (!"FABRIC_TDM_ROOT".equalsIgnoreCase(parentTable)) {
                        parentTable = TDM_DELETE_TABLES_PREFIX + parentTable;
                    }
		    	    map.put("PARENT_TABLE", parentTable);
		        	map.put("PARENT_FIELD_NAME", ri.from.get("column"));
			        map.put("FIELD_NAME", ri.to.get("column"));
			        map.put("FIELD_TYPE", luType.ludbObjects.get(tableName).getLudbColumnMap().get(ri.to.get("column")).columnType);
                    
                    //log.info("getPopArgumentListForDelete Adding Map for field: " +  map.get("FIELD_NAME") + " with type:"  + map.get("FIELD_TYPE"));
		    	    result.add(map);
		    	}
			    //break;
			}
		} else {
            Map<String, String> map = new HashMap<>();
            //log.info("getPopArgumentListForDelete REL IS NULL");
            map.put("PARENT_TABLE", "FABRIC_TDM_ROOT");
            map.put("PARENT_FIELD_NAME","IID");
            map.put("FIELD_NAME", getGlobal("ROOT_COLUMN_NAME", luName));
            map.put("FIELD_TYPE", "TEXT");
            result.add(map);
        }

        Map<String, List<LudbRelationInfo>> relChild = null;
        if (luType.getLudbPhysicalRelations() != null) {
		    relChild = luType.getLudbPhysicalRelations().get(tableName);
        }

		if (relChild != null) {
            //log.info("relChild IS NOT NULL, Handling table: <" + tableName + ">" + ", size of relChild: " + relChild.size() + ", keySet size: " + relChild.keySet().size());
            //log.info("relChild IS: " + relChild);
           //log.info("getPopArgumentListForDelete comparing - result: " + result);
			for (Object key : relChild.keySet()) {
                //log.info("getPopArgumentListForDelete handling key:"  + key);
                
			    for (LudbRelationInfo ri : (List<LudbRelationInfo>) relChild.get(key)) {
			        //log.info("getPopArgumentListForDelete handling ri.from:"  + ri.from.get("column"));
                    Boolean fieldFound = false;
                    for (Map<String, String> relMap : result) {
                        //log.info("getPopArgumentListForDelete - relMap: " + relMap);
                        if (relMap.get("FIELD_NAME").equals(ri.from.get("column"))) {
                            //log.info("getPopArgumentListForDelete match found");
                            fieldFound = true;
                            break;
                        }

                    }
                    if (!fieldFound) {
                        //log.info("getPopArgumentListForDelete Adding field: " + ri.from.get("column"));
			            Map<String, String> map = new HashMap<>();
                  
    		    	    map.put("PARENT_TABLE", "");
		        	    map.put("PARENT_FIELD_NAME", "");
			            map.put("FIELD_NAME", ri.from.get("column"));
			            map.put("FIELD_TYPE", luType.ludbObjects.get(tableName).getLudbColumnMap().get(ri.from.get("column")).columnType);
                    
                        //log.info("getPopArgumentListForDelete Adding Map with type:"  + map.get("FIELD_TYPE"));
		    	        result.add(map);
                    }
		    	}
			}
		}

		return result;
	}

    @out(name = "result", type = Set.class, desc = "")
	public static Set<Map<String,String>> getDeleteTablesFields(String luName, String dbInterfaceName, String catalogSchema, String tableName) throws Exception {
       
        LUType luType = LUType.getTypeByName(luName);
        Set<Map<String, String>> result = getPopArgumentListForDelete(luName, tableName);
        List<Map<String, Object>> tablePKs =  getDbTablePKs(dbInterfaceName, catalogSchema, tableName);
        int key_min = -1;
        //log.info("getDeleteTablesFields - init key_min: " + key_min);
        //log.info("getDeleteTablesFields - init result size: " + result.size());
        for (Map<String, Object> pkField : tablePKs) {
            //log.info("getDeleteTablesFields - Checking pk field: " + pkField.get("COLUMN_NAME") + ", with key: " + pkField.get("KEY_SEQ"));
            for (Map<String, String> map : result) {
                //log.info("getDeleteTablesFields - Checking result field: " + map.get("FIELD_NAME"));
                if (map.get("FIELD_NAME").equals(pkField.get("COLUMN_NAME").toString())) {
                    key_min = Integer.parseInt(pkField.get("KEY_SEQ").toString());
                    break;
                }
            }
            if(key_min == 1) {
                break;
            }
        }
        //log.info("getDeleteTablesFields - calculated key_min: " + key_min);
        if(key_min > 1) {
            //log.info("getDeleteTablesFields - looping over tablePKs");
            for (Map<String, Object> pkField : tablePKs) {
                int keySeq = Integer.parseInt(pkField.get("KEY_SEQ").toString());
                //log.info("getDeleteTablesFields - handling keySeq: " + keySeq);
                if (keySeq < key_min) {
                    Map<String, String> map = new HashMap<>();
                    String fieldName = pkField.get("COLUMN_NAME").toString();
                    map.put("PARENT_TABLE", "");
                    map.put("PARENT_FIELD_NAME", "");
                    map.put("FIELD_NAME", fieldName);
                    map.put("FIELD_TYPE", luType.ludbObjects.get(tableName).getLudbColumnMap().get(fieldName).columnType);
                    //log.info("getDeleteTablesFields - Adding Field: " + fieldName);
                    result.add(map);
                }
            }

        }
        
        return result;
    }
	@out(name = "result", type = List.class, desc = "")
	public static List<Map<String,String>> filerOutSequences(List<Map<String,String>> Sequences, List<Map<String,String>> parentRec) throws Exception {
		List<Map<String, String>> result = new ArrayList<Map<String, String>>(Sequences);
		
		for (Map<String, String> parentMap : parentRec) {
			for (Map<String, String> seqMap : Sequences) {
				if (seqMap.get("TARGET_FIELD_NAME").equalsIgnoreCase(parentMap.get("FIELD_NAME"))) {
					result.remove(seqMap);
					break;
				}
			}
		}
		
		return result;
	}

	@out(name = "pks", type = List.class, desc = "")
	public static List<Map<String, Object>> getDbTablePKs(String dbInterfaceName, String catalogSchema, String table) throws Exception {
		ResultSet rs = null;
		ResultSet rs1 = null;
		String[] types = {"TABLE"};
		String targetTableName = table;
		
		try {
			DatabaseMetaData md = getConnection(dbInterfaceName).getMetaData();
			
			String[] dbSchemaType = getDBCollection(md, catalogSchema);
			String catalog = dbSchemaType[0];
			String schema = dbSchemaType[1];
			//log.info("getDbTableColumns - Catalog: " + catalog + ", Schema: " + schema);
			rs = md.getTables(catalog, schema, "%", types);
			
			while (rs.next()) {
				if (table.equalsIgnoreCase(rs.getString(3))) {
					targetTableName = rs.getString(3);
					//log.info("getDbTableColumns - tableName: " + targetTableName);
					break;
				}
			}

			// get PKs
			rs1 = md.getPrimaryKeys(catalog, schema, targetTableName);
			List<Map<String, Object>> pkList = new ArrayList<>();
			while (rs1.next()) {
                Map <String, Object> map = new HashMap<>();
                map.put("COLUMN_NAME", rs1.getString("COLUMN_NAME"));
                map.put("KEY_SEQ", rs1.getInt("KEY_SEQ"));
                //log.info("Adding PK field: " + map.get("COLUMN_NAME") + ", seq: " +  map.get("KEY_SEQ") );
				pkList.add(map);
			}

			return pkList;
		} finally {
			if (rs != null)
				rs.close();
			if (rs1 != null)
				rs1.close();
		}
	}

}
