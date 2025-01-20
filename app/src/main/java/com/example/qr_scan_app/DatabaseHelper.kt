import com.example.qr_scan_app.ActiveUser
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseHelper constructor(private val login: String, private val password:String) {

    private val dbConnectjTDS = "jdbc:jtds:sqlserver://io-poczta.database.windows.net:1433/PocztaDB;ssl=request;"
    private val dbDriverjTDS = "net.sourceforge.jtds.jdbc.Driver"

    private val connection: Connection? by lazy {
        try {
            Class.forName(dbDriverjTDS) // Load the driver
            DriverManager.getConnection(dbConnectjTDS, login, password)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    companion object {
        private var lastLogin: String = ""
        private var lastPassword: String = ""

        // Constructor using last credentials
        operator fun invoke(): DatabaseHelper {
            if (lastLogin.isEmpty() || lastPassword.isEmpty()) {
                throw IllegalStateException("No previous login and password available. Please provide credentials initially.")
            }
            return DatabaseHelper(lastLogin, lastPassword)
        }

        operator fun invoke(login: String, password: String): DatabaseHelper {
            lastLogin = login
            lastPassword = password
            return DatabaseHelper(login, password)
        }
    }
    suspend fun isConnected():Boolean{
        return withContext(Dispatchers.IO){
            connection
            invoke(login,password)
            val re = executeQuery("SELECT Imię,Nazwisko from dbo.Pracownicy WHERE [login] = '$login'")
            //val re = executeQuery("Select 1")
            if(re.isNullOrEmpty()){
                  false
            }
            else{
                ActiveUser.setUser(re?.get(0)?.getValue("Imię").toString(),re?.get(0)?.getValue("Nazwisko").toString(),login,password )
                true
            }
        }
    }
    suspend fun executeQuery(query: String, params: List<Any> = emptyList()): List<Map<String, Any>>? {
        return withContext(Dispatchers.IO) {
            val resultList = mutableListOf<Map<String, Any>>()
            try {
                val preparedStatement = connection?.prepareStatement(query)
                params.forEachIndexed { index, param ->
                    preparedStatement?.setObject(index + 1, param)
                }
                val resultSet = preparedStatement?.executeQuery()
                while (resultSet?.next() == true) {
                    val row = mutableMapOf<String, Any>()
                    for (i in 1..resultSet.metaData.columnCount) {
                        row[resultSet.metaData.getColumnName(i)] = resultSet.getObject(i)
                    }
                    resultList.add(row)
                }
                preparedStatement?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            resultList
        }
    }

    suspend fun executeUpdate(query: String, params: List<Any> = emptyList()): Int {
        return withContext(Dispatchers.IO) {
            var rowsAffected = 0
            try {
                val preparedStatement = connection?.prepareStatement(query)
                params.forEachIndexed { index, param ->
                    preparedStatement?.setObject(index + 1, param)
                }
                rowsAffected = preparedStatement?.executeUpdate() ?: 0
                preparedStatement?.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            rowsAffected
        }
    }

    fun closeConnection() {
        try {
            connection?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
