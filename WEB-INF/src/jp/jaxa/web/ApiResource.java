package jp.jaxa.web;

import static java.lang.String.format;
import static java.sql.DriverManager.getConnection;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

/**
 * 各APIに共通な変数やメソッドを定義するクラス
 * 
 * @author Takahiro Tsuchida
 */
public class ApiResource {
	protected static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

	@Context
	ServletContext context;

	/**
	 * 指定されたトークンが正しいものかどうかを判定する
	 * 
	 * @param token
	 * @return
	 */
	protected boolean isValidToken(String token) {
		if (token == null) {
			return false;
		}

		try {
			Connection con = loadConnection();
			PreparedStatement statement = con
					.prepareStatement("SELECT EXISTS (SELECT token FROM tokens WHERE token = ?)");
			statement.setString(1, token);

			ResultSet resultSet = statement.executeQuery();
			while (resultSet.next()) {
				return resultSet.getBoolean(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * データベースへの接続情報を設定ファイルから取得する
	 * 
	 * @return
	 * @throws IOException
	 * @throws SQLException
	 */
	protected Connection loadConnection() throws IOException, SQLException {
		Properties prop = new Properties();
		prop.load(context.getResourceAsStream("WEB-INF/conf/gcom_w1_db.ini"));

		String host = prop.getProperty("hostname");
		String port = prop.getProperty("port");
		String db = prop.getProperty("database");
		String user = prop.getProperty("user");
		String password = prop.getProperty("password");

		try {
			Class.forName("org.postgresql.Driver");
		} catch (ClassNotFoundException e) {
		}

		String url = format("jdbc:postgresql://%s:%s/%s", host, port, db);
		return getConnection(url, user, password);
	}
}
