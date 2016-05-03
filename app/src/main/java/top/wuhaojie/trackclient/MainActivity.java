package top.wuhaojie.trackclient;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.getbase.floatingactionbutton.FloatingActionButton;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    /**
     * 协调布局
     */
    private CoordinatorLayout mClMain;
    /**
     * 悬浮按钮——打开文件
     */
    private FloatingActionButton mFabOpenFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initViews();

    }

    /**
     * 初始化视图
     */
    private void initViews() {
        setSupportActionBar(mToolbar);
        mClMain = (CoordinatorLayout) findViewById(R.id.cl_main);
        mFabOpenFile = (FloatingActionButton) findViewById(R.id.fab_openfile);
        mFabOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(mClMain, "Hello World", Snackbar.LENGTH_SHORT).show();
            }
        });
    }


}
