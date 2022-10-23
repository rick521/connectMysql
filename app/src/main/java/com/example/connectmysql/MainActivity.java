package com.example.connectmysql;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    private StudentBean studentBean;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        executeAsyncTask(4);


    }
    private void executeAsyncTask(int i) {
        //Observable#create方法创建一个异步任务
        Observable.create(new ObservableOnSubscribe<Integer>() {
                    @Override
                    public void subscribe(@NonNull ObservableEmitter<Integer> e) throws Exception {

                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Connection conn =(Connection) DBOpenHelper.getConn();
                                Statement st = null;
                                try {st = (Statement) conn.createStatement();} catch (SQLException ex) {ex.printStackTrace();}
                                switch (i){
                                    case 1:
                                        String sql = "select * from student";
                                        try {
                                            ResultSet rs = st.executeQuery(sql);
                                            while (rs.next()){
                                                studentBean=new StudentBean();
                                                studentBean.setName(rs.getString("name"));
                                                studentBean.setId(rs.getInt("id"));
                                                studentBean.setAge(rs.getInt("age"));
                                                studentBean.setMajor(rs.getString("major"));
                                                e.onNext(0);
                                            }
                                            st.close();
                                            conn.close();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                        }
                                        break;
                                    case 2:
                                        String sqlInsert = "insert into student (id,name,age,major) values (?,?, ?,?)";
                                        try {
                                            PreparedStatement ps = conn.prepareStatement(sqlInsert);
                                            ps.setInt(1,202121103);
                                            ps.setString(2,"names");
                                            ps.setInt(3, 19);
                                            ps.setString(4,"cs");
                                            ps.executeUpdate();
                                            st.close();
                                            conn.close();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            Log.e("-->",e.getMessage());
                                        }
                                        e.onComplete();
                                        break;
                                    case 3:
                                        String sqlDele = "delete from student where id = ?";
                                        try {
                                            PreparedStatement ps = conn.prepareStatement(sqlDele);
                                            ps.setInt(1,202121101);
                                            ps.executeUpdate();
                                            st.close();
                                            conn.close();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            Log.e("-->",e.getMessage());
                                        }
                                        e.onComplete();
                                        break;
                                    case 4:
                                        String sqlUpdate = "update student set age = ? where id = ?";
                                        try {
                                            PreparedStatement ps = conn.prepareStatement(sqlUpdate);
                                            ps.setInt(1,22);
                                            ps.setInt(2,202120100);
                                            ps.executeUpdate();
                                            st.close();
                                            conn.close();
                                        } catch (SQLException e) {
                                            e.printStackTrace();
                                            Log.e("-->",e.getMessage());
                                        }
                                        e.onComplete();
                                        break;
                                }

                            }
                        }).start();
                    }
                })
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                    }

                    @Override
                    public void onNext(@NonNull Integer integer) {
                        Log.e("-->",studentBean.getName()+"\n"
                        +studentBean.getId()+"\n"
                        +studentBean.getAge()+"\n"
                        +studentBean.getMajor());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        Log.e("-->","COMP");

                    }
                });

    }
}