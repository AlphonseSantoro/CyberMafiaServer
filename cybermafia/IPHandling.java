package cybermafia;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class IPHandling {

    private RandomValue randomNumber;
    private ResultSet rs;

    public IPHandling(){
        randomNumber = new RandomValue();
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
        return "" + randomNumber.getRandomChar('A', 26) +
                randomNumber.getRandomChar('A', 26) +
                randomNumber.getRandomChar('A', 26) +
                randomNumber.getRandomChar('A', 26);
    }

    /**
     *
     * @param ip The IP address to check if it exist in DB.
     * @return Return true if the address exist in DB.
     */
    private boolean isIpInUse(String ip) throws SQLException {
        String ipStmt = "select ip from ip_list where ip = ?;";
        PreparedStatement checkIP = DBConnect.getConnection().prepareStatement(ipStmt);
        System.out.println(checkIP);
        checkIP.setString(1, ip);
        System.out.println(checkIP);
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
