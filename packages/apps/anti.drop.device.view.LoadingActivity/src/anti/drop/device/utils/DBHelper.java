package anti.drop.device.utils;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import anti.drop.device.pojo.DeviceBean;

public class DBHelper extends SQLiteOpenHelper {

	private final String TAG = DBHelper.class.getSimpleName();

	private static final String DB_NAME = "anti";
	private static final int DB_VERSION = 1;
	private SQLiteDatabase db;
	private static DBHelper instance;

	private static final String TABLE_DEVICE = "device";
	private static final String CREATE_TABLE_DEVICE = "create table "
			+ TABLE_DEVICE
			+ " (_id integer primary key autoincrement,address text,name text,"
			+ "status integer,bell text)";

	public DBHelper(Context mContext) {
		super(mContext, DB_NAME, null, DB_VERSION);
	}

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	public static DBHelper getInstance(Context mContext) {
		if (instance == null) {
			instance = new DBHelper(mContext);
		}
		return instance;
	}

	// 初始化数据库，打开对数据库的连接
	public void open() {
		if (db == null) {
			db = getWritableDatabase();
		}
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		this.db = db;
		operateTable(db, CREATE_TABLE_DEVICE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		if (oldVersion == newVersion) {
			return;
		}
		operateTable(db, "DROP TABLE IF EXISTS " + TABLE_DEVICE);
		onCreate(db);
	}

	private void operateTable(SQLiteDatabase db, String actionString) {
		db.execSQL(actionString);
	}

	// 添加一条记录
	public void insertDevice(DeviceBean device) {
		ContentValues param = new ContentValues();
		param.put("address", device.getAddress());
		param.put("name", device.getName());
		param.put("status", device.getStatus());
		param.put("bell", device.getBell());
		db.insert(TABLE_DEVICE, null, param);
	}

	// 删除一条记录
	public void deleteDevice(DeviceBean device) {
		db.delete(TABLE_DEVICE, "address=?", new String[] { device.address });
	}

	// 查询所有的数据
	public ArrayList<DeviceBean> query() {
		ArrayList<DeviceBean> data = new ArrayList<DeviceBean>();
		Cursor cur = null;
		try {
			cur = db.query(TABLE_DEVICE, null, null, null, null, null, null);
			cur.moveToFirst();
			for (int i = 0; i < cur.getCount(); i++) {

				DeviceBean device = new DeviceBean();
				device.address = cur.getString(1);
				device.name = cur.getString(2);
				device.status = cur.getInt(3);
				device.bell = cur.getString(4);
				data.add(device);
				cur.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if (cur != null) {
					cur.close();
				}
			} catch (Exception e) {
				Log4L.e(TAG, e.toString());
			}
		}

		return data;
	}

	public DeviceBean query2(String address) {
		DeviceBean data = new DeviceBean();
		Cursor cur = null;
		try {
			cur = db.query(TABLE_DEVICE, null, null, null, null, null, null);
			cur.moveToFirst();
			for (int i = 0; i < cur.getCount(); i++){
				if(cur.getString(1).equals(address)){
					data.address = cur.getString(1);
					data.name = cur.getString(2);
					data.status = cur.getInt(3);
					data.bell = cur.getString(4);
				}
				cur.moveToNext();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (cur != null) {
					cur.close();
				}
			} catch (Exception e) {
				Log4L.e(TAG, e.toString());
			}
		}
		return data;
	}

	/**
	 * 修改状态
	 * 
	 * @param device
	 * @param status
	 */
	public void alter(DeviceBean device, int status) {
		String address = device.getAddress();
		ContentValues values = new ContentValues();
		values.put("status", status);
		db.update(TABLE_DEVICE, values, "address=?", new String[] { address });
	}

	/**
	 * 修改铃声
	 * 
	 * @param device
	 * @param bell
	 */
	public void alter(DeviceBean device, String bell) {
		String address = device.getAddress();
		ContentValues values = new ContentValues();
		values.put("bell", bell);
		db.update(TABLE_DEVICE, values, "address=?", new String[] { address });
	}

	/**
	 * 修改
	 * 
	 * @param device
	 * @param bell
	 */
	public void alter(DeviceBean device, String key, String value) {
		String address = device.getAddress();
		ContentValues values = new ContentValues();
		values.put(key, value);
		db.update(TABLE_DEVICE, values, "address=?", new String[] { address });
	}

	/**
	 * 删除整张表数据
	 */
	public void deleteAll(){
		ArrayList<DeviceBean> data = new ArrayList<DeviceBean>();
		Cursor cur = null;
		try {
			cur = db.query(TABLE_DEVICE, null, null, null, null, null, null);
			cur.moveToFirst();
			
			for (int i = 0; i < cur.getCount(); i++){
				DeviceBean device = new DeviceBean();
				device.address = cur.getString(1);
				device.name = cur.getString(2);
				device.status = cur.getInt(3);
				device.bell = cur.getString(4);
				deleteDevice(device);
				cur.moveToNext();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			try {
				if (cur != null) {
					cur.close();
				}
			} catch (Exception e) {
				Log4L.e(TAG, e.toString());
			}
		}
	}

}
