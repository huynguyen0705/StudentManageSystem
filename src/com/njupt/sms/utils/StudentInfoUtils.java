package com.njupt.sms.utils;

import com.njupt.sms.Session;
import com.njupt.sms.beans.Student;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class StudentInfoUtils {
    private JdbcUtils jdbcUtils;

    public StudentInfoUtils() {
        jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
    }

    public boolean saveStudentInfo(Student student) {
        String sql = "update student set age = ? ,sex = ? ,birthday = ?, address = ? , phone = ?, email = ?,studentClass = ? where studentCode = ? ";
        List<Object> params = new ArrayList<>();
        params.add(student.getAge());
        params.add(student.getSex());
        params.add(student.getBirthday());
        params.add(student.getAddress());
        params.add(student.getPhone());
        params.add(student.getEmail());
        params.add(student.getStudentClass());
        params.add(student.getStudentCode());

        boolean flag = false;
        try {
            flag = jdbcUtils.updateByPreparedStatement(sql, params);
            if (flag) {
                Student newStudent = findStudentInfoByUsername(((Student)Session.userInfo).getUsername());
                Session.userInfo = newStudent;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return flag;

    }

    public Student findStudentInfoByUsername(String username) {
        String sql = "select * from student where username = ?";
        List<Object> params = new ArrayList<>();
        params.add(username);
        Student student = null;
        try {
            student = jdbcUtils.findSimpleRefResult(sql, params, Student.class);
            if (student != null) {
                Session.userInfo = student;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return student;
    }

    public List<Map<String, Object>> getAllStudentsInfo() {
        String sql = "select * from student";
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = jdbcUtils.findModeResult(sql, null);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


    public boolean deleteStudentById(int id) {
        String sql = "delete from student where id = ?";
        List<Object> params = new ArrayList<>();
        params.add(id);
        boolean flag = false;
        try {
            flag = jdbcUtils.updateByPreparedStatement(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean saveStudentInfoByMap(Map<String, Object> map) {
        String sql = "";
        if (map.containsKey("id")) {
            sql = "update student set username = ?,password = ?,studentCode = ? ,name = ?, " +
                    "studentClass = ? , age = ?,sex = ?,birthday = ?,address = ?,phone = ? ," +
                    "email = ? where id = ?";
        } else {
            sql = "insert into student(username,password,studentCode,name,studentClass,age,sex," +
                    "birthday,address,phone,email) values (?,?,?,?,?,?,?,?,?,?,?)";
        }
        List<Object> params = new ArrayList<>();
        params.add(map.get("username"));
        params.add(map.get("password"));
        params.add(map.get("studentCode"));
        params.add(map.get("name"));
        params.add(map.get("studentClass"));
        params.add(Integer.parseInt(map.get("age").toString().trim()));
        params.add(map.get("sex"));
        params.add(map.get("birthday"));
        params.add(map.get("address"));
        params.add(map.get("phone"));
        params.add(map.get("email"));
        if (map.containsKey("id")) {
            params.add(map.get("id"));
        }

        boolean flag = false;
        try {
            flag = jdbcUtils.updateByPreparedStatement(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;

    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        if (jdbcUtils != null) {
            jdbcUtils.releaseConn();
            jdbcUtils = null;

        }
        System.out.println(this.getClass().toString() + "đã đóng");
    }
}
