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
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import top.wuhaojie.trackclient.constant.Constants;
import top.wuhaojie.trackclient.utils.FileUtils;
import top.wuhaojie.trackclient.utils.HttpUtils;

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
    private Button mBtnUpload;
    private String mFilePath;
    private ProgressDialog mProgressDialog;
    private Button mBtnReceive;
    private boolean mIsFinishedCoverted = false;
    private String mFileName;

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
        mBtnUpload = (Button) findViewById(R.id.btn_upload);
        mTextContent = (TextView) findViewById(R.id.tv_content);
        mClMain = (CoordinatorLayout) findViewById(R.id.cl_main);
        mBtnReceive = (Button) findViewById(R.id.btn_receive);
        mFabOpenFile = (FloatingActionButton) findViewById(R.id.fab_openfile);
        mFabOpenFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOpenFile();
            }
        });
        mFileOpenProgressDialog = new ProgressDialog(this);
        mFileOpenProgressDialog.setTitle("请稍候...");
        mBtnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadFile();

            }
        });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mBtnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mFilePath) || TextUtils.isEmpty(mFileName) || !mIsFinishedCoverted) {
                    Snackbar.make(mClMain, "请先上传文件", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                Uri uri = Uri.parse(Constants.DOWNLOAD_ADDR);
                Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(downloadIntent);
            }
        });
    }

    /**
     * 文件上传
     */
    private void uploadFile() {

        if (TextUtils.isEmpty(mFilePath) || !new File(mFilePath).exists()) {
            showFileError();
            return;
        }

//        mProgressDialog.setTitle("请稍候");
//        mProgressDialog.setTitle("请稍候");
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setMessage("正在上传文件");
        mProgressDialog.show();


        String[] fileStrs = mFilePath.split("/");
        mFileName = fileStrs[fileStrs.length - 1];

        try {
            HttpUtils.uploadFile(new File(mFilePath), Constants.UPLOAD_ADDR, new AsyncHttpResponseHandler() {

                private boolean isFirst = true;

                @Override
                public void onSuccess(int i, Header[] headers, byte[] bytes) {
//                    requestProgress();
                    mProgressDialog.dismiss();
                    Snackbar.make(mClMain, "上传完成, 等待云端处理数据", Snackbar.LENGTH_SHORT).show();
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Snackbar.make(mClMain, "云端数据处理完成, 可以取回数据", Snackbar.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }, 2000);
                    mIsFinishedCoverted = true;
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    mProgressDialog.dismiss();
                    Snackbar.make(mClMain, "上传失败，请稍后再试", Snackbar.LENGTH_SHORT).show();
                }


                @Override
                public void onProgress(long bytesWritten, long totalSize) {
                    super.onProgress(bytesWritten, totalSize);
                    if (isFirst) {
                        isFirst = false;
                        mProgressDialog.setMax((int) totalSize);
                    }
                    mProgressDialog.setProgress((int) bytesWritten);

                    if (bytesWritten >= totalSize) {
                        mProgressDialog.dismiss();
                        mProgressDialog = new ProgressDialog(MainActivity.this);
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setMessage("正在转换数据，请稍等...");
                        mProgressDialog.show();
                    }

                }
            });
        } catch (FileNotFoundException e) {
            showFileError();
        }

    }

    private void requestProgress() {

        mProgressDialog.setTitle("正在转换文件(2/2)");
//        mProgressDialog.setProgress(0);

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                String s = HttpUtils.requestResponse(Constants.PROGRESS_ADDR);
                if (s != null && !s.isEmpty() && !"null".equals(s)) {
                    final String[] progress = s.split("#");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            mProgressDialog.setMax(Integer.parseInt(progress[1]));
                            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
                        }
                    });
                } else {
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mProgressDialog.setProgress(mProgressDialog.getProgress() + 1);
                    }
                });
            }
        };
        new Timer().schedule(timerTask, 0, 500);
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
                    mFilePath = FileUtils.getPath(this, uri);
                    Log.d(TAG, "FILE PATH = " + (mFilePath == null ? "null" : mFilePath) + " URI = " + uri.getScheme());
                    if (mFilePath == null) {
                        showFileError();
                        return;
                    }
                    // 获取文本内容并显示
                    setFileContent(mFilePath, mTextContent, mFileOpenProgressDialog);
                } else {
                    showFileError();
                }
                break;
        }


        super.onActivityResult(requestCode, resultCode, data);

    }

    private void showFileError() {
        if (mFileOpenProgressDialog != null && mFileOpenProgressDialog.isShowing())
            mFileOpenProgressDialog.dismiss();
        if (mProgressDialog != null && mProgressDialog.isShowing())
            mProgressDialog.dismiss();
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
                    while ((line = reader.readLine()) != null && (i++) <= 300) {
                        final String finalLine = line;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                textContent.append(finalLine + "\n");
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
