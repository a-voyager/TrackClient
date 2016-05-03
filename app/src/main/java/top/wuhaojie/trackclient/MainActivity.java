package top.wuhaojie.trackclient;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.wuhaojie.trackclient.constant.Constants;
import top.wuhaojie.trackclient.utils.FileUtils;

public class MainActivity extends AppCompatActivity {

    /**
     * 添加文本响应码
     */
    private static final int APPEND_TEXT = 1000;
    public static final String TAG = "MainActivity";

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


    /**
     * 主界面文本框
     */
    TextView mTextContent;
    private ProgressDialog mFileOpenProgressDialog;

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
        mTextContent = (TextView) findViewById(R.id.tv_content);
        mClMain = (CoordinatorLayout) findViewById(R.id.cl_main);
        mFabOpenFile = (FloatingActionButton) findViewById(R.id.fab_openfile);
        mFabOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOpenFile();
            }
        });
        mFileOpenProgressDialog = new ProgressDialog(this);
        mFileOpenProgressDialog.setTitle("请稍候...");
    }

    /**
     * 显示打开文件界面
     */
    private void showOpenFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(Intent.createChooser(intent, "选择轨迹文件(*.xml)"), Constants.FILE_SELECT_CODE);
    }


    /**
     * 选择文件是否成功的处理
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case Constants.FILE_SELECT_CODE:
                // 是否正确选择文件
                if (resultCode == RESULT_OK) {
                    // 获取数据
                    Uri uri = data.getData();
                    // 获取文件路径
                    String path = FileUtils.getPath(this, uri);
                    Log.d(TAG, "FILE PATH = " + (path == null ? "null" : path) + " URI = " + uri.getScheme());
                    if (path == null) {
                        showFileError();
                        return;
                    }
                    // 获取文本内容并显示
                    setFileContent(path, mTextContent, mFileOpenProgressDialog);
                } else {
                    showFileError();
                }
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    private void showFileError() {
        Snackbar.make(mClMain, "请选择正确的文件！", Snackbar.LENGTH_SHORT).show();
    }

    private void setFileContent(final String path, final TextView textContent, final ProgressDialog dialog) {
        dialog.show();
        mTextContent.setText("");
//        try {
//            File file = new File(path);
//            BufferedReader reader = new BufferedReader(new FileReader(file));
//            String line;
//            while ((line = reader.readLine()) != null) {
//                textContent.append(line + "\n");
//            }
//            fileOpenProgressDialog.dismiss();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        new Thread(new Runnable() {
            @Override
            public void run() {

                File file = new File(path);
                try {
                    BufferedReader reader = new BufferedReader(new FileReader(file));
                    String line = null;
                    int i = 0;
                    while ((line = reader.readLine()) != null) {
                        final String finalLine = line;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textContent.append(finalLine+"\n");
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        }).start();
    }


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case APPEND_TEXT:
                    break;
            }
        }
    };

}
