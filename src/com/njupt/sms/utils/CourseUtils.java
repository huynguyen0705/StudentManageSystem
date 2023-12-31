package com.njupt.sms.utils;

import javax.sound.midi.Soundbank;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CourseUtils {
    private JdbcUtils jdbcUtils;

    public CourseUtils() {
        jdbcUtils = new JdbcUtils();
        jdbcUtils.getConnection();
    }


    public List<Map<String, Object>> findAllCourse() {
        String sql = "select c.id as id ,courseName,academicYear,term ," +
                "teacherId,name , commitStatus from course as c ,teacher as t where teacherId = t.id";

        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = jdbcUtils.findModeResult(sql, null);
           // System.out.println(list);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> findAllCoursesByTeacherId(int teacherId) {
        String sql = "select * from course where teacherId = ?";
        List<Object> params = new ArrayList<>();
        params.add(teacherId);

        List<Map<String, Object>> list = null;
        try {
            list = jdbcUtils.findModeResult(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean saveCourse(Map<String, Object> map) {
        String sql = "";
        if (map.containsKey("id")) {
            sql = "update course set courseName = ? , academicYear = ?, term = ?,teacherId = ? where id = ?";
        } else {
            sql = "insert into course(courseName,academicYear,term,teacherId) values(?,?,?,?)";
        }

        List<Object> params = new ArrayList<>();
        params.add(map.get("courseName"));
        params.add(map.get("academicYear"));
        params.add(map.get("term"));
        params.add(map.get("teacherId"));
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

    public boolean removeCourse(int id) {
        String sql = "delete from course where id = ?";
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

    public List<Map<String, Object>> findAllStudentWithGradeByCourseId(int courseId) {
        String sql = "select s.id as id, studentCode ,name ,courseId from student as s ," +
                " chooseCourse as c where s.id = c.studentId and courseId = ? order by s.id asc";

        List<Object> params = new ArrayList<>();
        params.add(courseId);
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = jdbcUtils.findModeResult(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        String sql2 = "select * from grade where courseId = ? order by id asc";
        List<Map<String, Object>> grades = new ArrayList<>();

        try {
            grades = jdbcUtils.findModeResult(sql2, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> willReturnMap = list.get(i);
            if (grades.size() == 0) {
                willReturnMap.put("score", null);
            } else {
                for (int j = 0; j < grades.size(); j++) {
                    Map<String, Object> map = grades.get(j);
                    if ( map.get("studentId").equals(willReturnMap.get("id"))) {
                        willReturnMap.put("score", map.get("score"));
                    }
                }
            }

        }

        return list;
    }

    public List<Map<String, Object>> findAllStudentWithGradeDraftByCourseId(int courseId) {
        String sql = "select s.id as id, studentCode ,name ,courseId from student as s ," +
                " chooseCourse as c where s.id = c.studentId and courseId = ? order by s.id asc";

        List<Object> params = new ArrayList<>();
        params.add(courseId);
        List<Map<String, Object>> list = new ArrayList<>();

        try {
            list = jdbcUtils.findModeResult(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql2 = "select * from gradeDraft where courseId = ? order by id asc";
        List<Map<String, Object>> draftGrades = new ArrayList<>();

        try {
            draftGrades = jdbcUtils.findModeResult(sql2, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> willReturnMap = list.get(i);
            if (draftGrades.size() == 0) {
                willReturnMap.put("score", null);
            } else {
                for (int j = 0; j < draftGrades.size(); j++) {
                    Map<String, Object> map = draftGrades.get(j);
                    if (map != null && map.get("studentId").equals(willReturnMap.get("id"))) {
                        willReturnMap.put("score", map.get("score"));
                    }
                }
            }

        }

        return list;
    }

    public boolean commitCourseByCourseId(int courseId) {
        String sql = "update course set commitStatus = 'Đã chốt sổ' where id = ?";
        List<Object> params = new ArrayList<>();
        params.add(courseId);
        boolean flag = false;
        try {
            flag = jdbcUtils.updateByPreparedStatement(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }

    public boolean draftCourseByCourseId(int courseId) {
        String sql = "update course set commitStatus = 'Bản nháp' where id = ?";
        List<Object> params = new ArrayList<>();
        params.add(courseId);
        boolean flag = false;
        try {
            flag = jdbcUtils.updateByPreparedStatement(sql, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return flag;
    }


    public void clearCommitStautsByCourseId(int courseId) {

        String sql = "update course set commitStatus = '' where id = ?";
        String sql2 = "delete from gradeDraft where courseId = ?";
        String sql3 = "delete from grade where courseId = ?";

        List<Object> params = new ArrayList<>();
        params.add(courseId);

        try {
            jdbcUtils.updateByPreparedStatement(sql, params);
            jdbcUtils.updateByPreparedStatement(sql2, params);
            jdbcUtils.updateByPreparedStatement(sql3, params);
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
