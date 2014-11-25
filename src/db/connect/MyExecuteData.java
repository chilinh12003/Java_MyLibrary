package db.connect;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import db.define.DBConfig;

public class MyExecuteData
{
	Connection mConnect;

	PreparedStatement stmt = null;
	ResultSet rs = null;
	CallableStatement call_stmt = null;

	/**
	 * Cho phep su dung transaction hay khong
	 */
	public boolean UseTransaction = false;

	public DBConfig mDBConfig = null;

	public MyExecuteData(DBConfig mDBConfig) throws Exception
	{
		try
		{
			this.mDBConfig = mDBConfig;
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}
	
	public Connection getConnection() throws SQLException, Exception
	{
		return MyConnection.getConnection(mDBConfig);
	}

	public void CloseAll() throws Exception
	{
		try
		{
			if (rs != null) rs.close();

			if (call_stmt != null) call_stmt.close();

			if (stmt != null) stmt.close();

			if (mConnect != null) mConnect.close();
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	

	public Boolean Execute_Query(String Query) throws Exception
	{
		try
		{
			mConnect = getConnection();
			if (UseTransaction)
			{
				mConnect.setAutoCommit(false);
			}
			else
			{
				mConnect.setAutoCommit(true);
			}

			stmt = mConnect.prepareStatement(Query);
			int Result = stmt.executeUpdate();

			if (UseTransaction) mConnect.commit();

			if (Result > 0) return true;
			else return false;
		}
		catch (SQLException e)
		{
			try
			{
				if (mDBConfig.AutoSwitchConnection && mDBConfig.OrderConnection <= mDBConfig.MaxConnectionBackup)
				{
					if (	e.getSQLState().equalsIgnoreCase(DBConfig.SQLState.SQLState_TimeOut.GetValue()) || 
							e.getErrorCode() == DBConfig.SQLErrorCode.SQLErrorCode_TimeOut.GetValue())
					{
						mDBConfig.SetOrderConnection();
						return Execute_Query(Query);
					}
				}
				if (UseTransaction) mConnect.rollback();
				throw e;
			}
			catch (Exception ex)
			{
				try
				{
					if (UseTransaction) mConnect.rollback();
				}
				catch (Exception ex_1)
				{
					throw ex_1;
				}
				throw ex;
			}			
		}
		catch (Exception ex)
		{
			try
			{
				if (UseTransaction) mConnect.rollback();
			}
			catch (Exception ex_1)
			{
				throw ex_1;
			}

			throw ex;
		}
		finally
		{
			CloseAll();
		}
	}

	/**
	 * Update voi List gia tri duoc truyen vao
	 * 
	 * @param QueryFormat
	 *            Chuoi query duoc dinh danh theo tempate: INSERT INTO Table(ID)
	 *            VALUES(?)
	 * @param ValueList
	 *            Danh sach gia tri can truyen vao, theo dung thu tu cua
	 *            QueryFormat
	 * @return
	 * @throws Exception
	 */
	public Boolean Execute_Query(String QueryFormat, Object[] ValueList) throws Exception
	{
		try
		{
			mConnect = getConnection();
			if (UseTransaction)
			{
				mConnect.setAutoCommit(false);
			}
			else
			{
				mConnect.setAutoCommit(true);
			}

			stmt = mConnect.prepareStatement(QueryFormat);

			int i = 1;
			for (Object item : ValueList)
			{
				if (item instanceof Integer)
				{
					stmt.setInt(i, (Integer) item);
				}
				else if (item instanceof Double)
				{
					stmt.setDouble(i, (Double) item);
				}
				else if (item instanceof BigDecimal)
				{
					stmt.setBigDecimal(i, (BigDecimal) item);
				}
				else if (item instanceof Float)
				{
					stmt.setFloat(i, (Float) item);
				}
				else if (item instanceof String)
				{
					stmt.setString(i, (String) item);
				}
				else if (item instanceof Timestamp)
				{
					stmt.setTimestamp(i, (Timestamp) item);
				}
				else
				{
					boolean IsError = false;
					try
					{
						stmt.setObject(i, item);
					}
					catch (Exception ex)
					{
						IsError = true;
						System.out.print(ex.getMessage());
					}
					if (IsError)
					{
						stmt.setString(i, item.toString());
					}
				}

				i++;
			}
			int Result = stmt.executeUpdate();

			if (UseTransaction) mConnect.commit();

			if (Result > 0) return true;
			else return false;
		}
		catch (SQLException e)
		{
			try
			{
				if (mDBConfig.AutoSwitchConnection && mDBConfig.OrderConnection <= mDBConfig.MaxConnectionBackup)
				{
					if (	e.getSQLState().equalsIgnoreCase(DBConfig.SQLState.SQLState_TimeOut.GetValue()) || 
							e.getErrorCode() == DBConfig.SQLErrorCode.SQLErrorCode_TimeOut.GetValue())
					{
						mDBConfig.SetOrderConnection();
						return Execute_Query(QueryFormat,ValueList);
					}
				}
				if (UseTransaction) mConnect.rollback();
				throw e;
			}
			catch (Exception ex)
			{
				try
				{
					if (UseTransaction) mConnect.rollback();
				}
				catch (Exception ex_1)
				{
					throw ex_1;
				}
				throw ex;
			}			
		}
		catch (Exception ex)
		{
			try
			{
				if (UseTransaction) mConnect.rollback();
			}
			catch (Exception ex_1)
			{
				throw ex_1;
			}

			throw ex;
		}
		finally
		{
			CloseAll();
		}
	}

	public boolean Execute_Pro(String ProName, String[] Arr_Name, String[] Arr_Value) throws Exception
	{
		try
		{

			if (Arr_Name.length != Arr_Value.length) { throw new Exception(
					"Arr_Name va Arr_Value co doi dai mang la khac nhau"); }

			mConnect = getConnection();

			if (UseTransaction)
			{
				mConnect.setAutoCommit(false);
			}
			else
			{
				mConnect.setAutoCommit(true);
			}

			String ListFormat = "";
			for (int i = 0; i < Arr_Name.length; i++)
			{
				if (ListFormat.length() > 0) ListFormat += ",";
				ListFormat += "?";
			}

			call_stmt = mConnect.prepareCall("{call " + ProName + "(" + ListFormat + ")}");

			for (int i = 0; i < Arr_Name.length; i++)
			{
				call_stmt.setString(Arr_Name[i], Arr_Value[i]);
			}

			int Result = call_stmt.executeUpdate();

			if (UseTransaction) mConnect.commit();

			if (Result > 0) return true;
			else return false;

		}
		catch (SQLException e)
		{
			try
			{
				if (mDBConfig.AutoSwitchConnection && mDBConfig.OrderConnection <= mDBConfig.MaxConnectionBackup)
				{
					if (	e.getSQLState().equalsIgnoreCase(DBConfig.SQLState.SQLState_TimeOut.GetValue()) || 
							e.getErrorCode() == DBConfig.SQLErrorCode.SQLErrorCode_TimeOut.GetValue())
					{
						mDBConfig.SetOrderConnection();
						return Execute_Pro(ProName,Arr_Name,Arr_Value);
					}
				}
				if (UseTransaction) mConnect.rollback();
				throw e;
			}
			catch (Exception ex)
			{
				try
				{
					if (UseTransaction) mConnect.rollback();
				}
				catch (Exception ex_1)
				{
					throw ex_1;
				}
				throw ex;
			}			
		}
		catch (Exception ex)
		{
			try
			{
				if (UseTransaction) mConnect.rollback();
			}
			catch (SQLException ex_1)
			{
				// throw ex_1;
			}
			throw ex;
		}
		finally
		{
			CloseAll();
		}
	}

}
