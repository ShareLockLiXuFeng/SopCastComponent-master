package com.laifeng.sopcastsdk.controller.video;

import android.os.Build;
import android.os.MessageQueue;
import android.widget.SeekBar;
import android.widget.TextView;

import com.laifeng.sopcastsdk.configuration.VideoConfiguration;
import com.laifeng.sopcastsdk.constant.SopCastConstant;
import com.laifeng.sopcastsdk.controller.video.IVideoController;
import com.laifeng.sopcastsdk.utils.SopCastLog;
import com.laifeng.sopcastsdk.video.MyRecorder;
import com.laifeng.sopcastsdk.video.MyRenderer;
import com.laifeng.sopcastsdk.video.OnVideoEncodeListener;

import org.w3c.dom.Text;

/**
 * @Title: CameraVideoController
 * @Package com.laifeng.sopcastsdk.controller.video
 * @Description:   摄像头录制视频的控制器
 * @Author Jim
 * @Date 16/9/14
 * @Time 下午12:54
 * @Version
 */
public class CameraVideoController implements IVideoController {
    private MyRecorder mRecorder;
    private MyRenderer mRenderer;
    private VideoConfiguration mVideoConfiguration = VideoConfiguration.createDefault();
    private OnVideoEncodeListener mListener;

    public CameraVideoController(MyRenderer renderer) {
        mRenderer = renderer;
        mRenderer.setVideoConfiguration(mVideoConfiguration);
    }

    public void setVideoConfiguration(VideoConfiguration configuration) {
        mVideoConfiguration = configuration;
        mRenderer.setVideoConfiguration(mVideoConfiguration);
    }

    public void setVideoEncoderListener(OnVideoEncodeListener listener) {
        mListener = listener;
    }

    /**
     * 开始
     *
     */
    public void start() {
        if(mListener == null) {
            return;
        }
        SopCastLog.d(SopCastConstant.TAG, "Start video recording");
        mRecorder = new MyRecorder(mVideoConfiguration);
        mRecorder.setVideoEncodeListener(mListener);
        mRecorder.prepareEncoder();
        mRenderer.setRecorder(mRecorder);
    }

    /**
     * 停止
     */
    public void stop() {
        SopCastLog.d(SopCastConstant.TAG, "Stop video recording");
        mRenderer.setRecorder(null);
        if(mRecorder != null) {
            mRecorder.setVideoEncodeListener(null);
            mRecorder.stop();
            mRecorder = null;
        }
    }

    /**
     * 暂停
     *
     */
    public void pause() {
        SopCastLog.d(SopCastConstant.TAG, "Pause video recording");
        if(mRecorder != null) {
            mRecorder.setPause(true);
        }
    }

    /**
     * 开始
     */

    public void resume() {
        SopCastLog.d(SopCastConstant.TAG, "Resume video recording");
        if(mRecorder != null) {
            mRecorder.setPause(false);
        }
    }

    /**
     * 设置编码
     * @param bps
     * @return
     */

    public boolean setVideoBps(int bps) {
        //重新设置硬编bps，在低于19的版本需要重启编码器
        boolean result = false;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            //由于重启硬编编码器效果不好，此次不做处理
            SopCastLog.d(SopCastConstant.TAG, "Bps need change, but MediaCodec do not support.");
        }else {
            if (mRecorder != null) {
                SopCastLog.d(SopCastConstant.TAG, "Bps change, current bps: " + bps);
                mRecorder.setRecorderBps(bps);
                result = true;
            }
        }
        return result;
    }
}
