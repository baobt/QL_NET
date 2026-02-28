/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QLTN.Dao;

import QLTN.Entity.ComputerGroup;
import QLTN.Utils.JdbcHelperQLTN;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author AD
 */
public class ComputerGroupDao extends QLTNSysDao<ComputerGroup, String> {

    String INSERT_SQL = "INSERT INTO ComputerGroup VALUES(?,?,?,?)";
    String UPDATE_SQL = "UPDATE ComputerGroup SET name = ?, description = ?, hourly_rate = ?, status = ?, WHERE group_id = ?";
    String DELETE_SQL = "DELETE FROM ComputerGroup WHERE group_id = ?";
    String SELECT_ALL_SQL = "SELECT * FROM ComputerGroup";
    String SELECT_BY_ID_SQL = "SELECT *FROM ComputerGroup WHERE group_id = ?";

    @Override
    public void insert(ComputerGroup entity) {
        JdbcHelperQLTN.update(INSERT_SQL, entity.getName(), entity.getDescription(), entity.getHourlyRate(), entity.getStatus());
    }

    @Override
    public void update(ComputerGroup entity) {
        JdbcHelperQLTN.update(UPDATE_SQL, entity.getName(), entity.getDescription(), entity.getHourlyRate(), entity.getStatus());
    }

    @Override
    public void delete(String id) {
        JdbcHelperQLTN.update(DELETE_SQL, id);
    }

    @Override
    public List<ComputerGroup> selectAll() {
        return this.selectbySql(SELECT_ALL_SQL);
    }

    @Override
    public ComputerGroup SelectbyID(String id) {
        List<ComputerGroup> list = this.selectbySql(SELECT_BY_ID_SQL, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<ComputerGroup> selectbySql(String sql, Object... args) {
        List<ComputerGroup> list = new ArrayList<ComputerGroup>();
        try {
            ResultSet rs = JdbcHelperQLTN.query(sql, args);
            while (rs.next()) {
                ComputerGroup cpg = new ComputerGroup();
                cpg.setGroupId(rs.getInt("group_id"));
                cpg.setName(rs.getString("name"));
                cpg.setDescription(rs.getString("description"));
                cpg.setHourlyRate(rs.getBigDecimal("hourly_rate"));
                cpg.setStatus(rs.getString("status"));
                list.add(cpg);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ComputerGroup Login(String E, String K) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


}
