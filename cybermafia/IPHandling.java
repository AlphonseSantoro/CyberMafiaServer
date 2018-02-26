package cybermafia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IPHandling {

    private RandomValue randomLetters;
    private ResultSet rs;

    public IPHandling(){
        randomLetters = new RandomValue();
    }

    public String generateIPv7() throws SQLException {
        String ip = generateIpChunk() + "." + generateIpChunk() + "." +generateIpChunk() + "." + generateIpChunk();
        while(isIpInUse(ip)){
            ip = generateIpChunk() + "." + generateIpChunk() + "." +generateIpChunk() + "." + generateIpChunk();
        }
        return ip;
    }

    /**
    *   Generate a random IP with 4 chunks each with 4 letters.
    *   TODO: Too similar to IPv6. Find new format...
    */
    private String generateIpChunk(){
        return "" + randomLetters.getRandomChar('A', 26) +
                randomLetters.getRandomChar('A', 26) +
                randomLetters.getRandomChar('A', 26) +
                randomLetters.getRandomChar('A', 26);
    }

    /**
     *
     * @param ip The IP address to check if it exist in DB.
     * @return Return true if the address exist in DB.
     */
    private boolean isIpInUse(String ip) throws SQLException {
        String ipStmt = "SELECT IP FROM IP_List WHERE IP = ?;";
        PreparedStatement checkIP = DBConnect.getConnection().prepareStatement(ipStmt);
        checkIP.setString(1, ip);
        rs = DBConnect.selectStatement(checkIP);
        String rsIP;
        while (rs.next()){
            rsIP = rs.getString("ip");
            if(rsIP.equals(ip)){
                return true;
            }
        }
        return false;
    }
}
