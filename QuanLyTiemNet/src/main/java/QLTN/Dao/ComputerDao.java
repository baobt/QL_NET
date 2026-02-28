/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QLTN.Dao;

import QLTN.Entity.Computer;
import QLTN.Utils.JdbcHelperQLTN;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author AD
 */
public class ComputerDao extends QLTNSysDao<Computer, String> {

    String INSERT_SQL = "INSERT INTO Computer VALUES(?, ?, ?, ?, ?, ?);";
    String UPDATE_SQL = "UPDATE Computer SET group_id = ?, name = ?, status = ?, specifications = ?, ip_address = ?, position = ? WHERE computer_id = ?";
    String DELETE_SQL = "DELETE FROM Computer WHERE computer_id = ?";
    String SELECT_ALL_SQL = "SELECT * FROM Computer";
    String SELECT_BY_ID_SQL = "SELECT *FROM Computer WHERE computer_id = ?";
    String SELECT_BY_KEYWORD_SQL = "select * from Computer where name like ?";

    @Override
    public void insert(Computer entity) {
        JdbcHelperQLTN.update(INSERT_SQL, entity.getGroupId(), entity.getName(), entity.getStatus(), entity.getSpecifications(), entity.getIpAddress(), entity.getPosition());
    }

    @Override
    public void update(Computer entity) {
        JdbcHelperQLTN.update(UPDATE_SQL, entity.getGroupId(), entity.getName(), entity.getStatus(), entity.getSpecifications(), entity.getIpAddress(), entity.getPosition(), entity.getComputerId());

    }

    @Override
    public void delete(String id) {
        JdbcHelperQLTN.update(DELETE_SQL, id);
    }

    @Override
    public List<Computer> selectAll() {
        return this.selectbySql(SELECT_ALL_SQL);
    }

    @Override
    public Computer SelectbyID(String id) {
        List<Computer> list = this.selectbySql(SELECT_BY_ID_SQL, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<Computer> selectbySql(String sql, Object... args) {
        List<Computer> list = new ArrayList<Computer>();
        try {
            ResultSet rs = JdbcHelperQLTN.query(sql, args);
            while (rs.next()) {
                Computer cp = new Computer();
                cp.setComputerId(rs.getInt("computer_id"));
                cp.setGroupId(rs.getInt("group_id"));
                cp.setName(rs.getString("name"));
                cp.setStatus(rs.getString("status"));
                cp.setSpecifications(rs.getString("specifications"));
                cp.setIpAddress(rs.getString("ip_address"));
                cp.setPosition(rs.getInt("position"));
                list.add(cp);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Computer> selectByComputer(String masp) {
        String sql = "SELECT *FROM Computer WHERE computer_id = ?";
        return this.selectbySql(sql, masp);
    }

    public static ResultSet Getone(String idSP) {
        String sql = "SELECT *FROM Computer WHERE computer_id = ?";
        return JdbcHelperQLTN.query(sql, idSP);
    }

    public Computer GetComputer(String ID) {
        Computer cp = new Computer();
        ResultSet rs = Getone(ID);
        try {
            if (rs.next()) {
                cp.setComputerId(rs.getInt("computer_id"));
                cp.setGroupId(rs.getInt("group_id"));
                cp.setName(rs.getString("name"));
                cp.setStatus(rs.getString("status"));
                cp.setSpecifications(rs.getString("specifications"));
                cp.setIpAddress(rs.getString("ip_address"));
                cp.setPosition(rs.getInt("position"));
                return cp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Computer> selectNotInCouse(String group_id, String keyword) {
        String sql = "SELECT *FROM Computer WHERE name LIKE ? AND group_id IN (SELECT * FROM Computer)";
        return this.selectbySql(sql, "%" + keyword + "%", group_id);
    }

    public List<Computer> selectByKeyword(String keyWord) {
        return selectbySql(SELECT_BY_KEYWORD_SQL, "%" + keyWord + "%");
    }

    @Override
    public Computer Login(String E, String K) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
