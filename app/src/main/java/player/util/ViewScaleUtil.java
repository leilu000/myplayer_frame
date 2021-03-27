package player.util;

/**
 * 根据缩放模式来计算宽高
 * Created by ll on 2019/7/22.
 */
public class ViewScaleUtil {

    public enum ScaleMode {
        AspectFit, CenterCrop, Fill
    }

    public static class Size {
        public int x;
        public int y;
        public int width;
        public int height;
    }

    public static Size calcFitSize(int imageWidth, int imageHeight, int viewWidth
            , int viewHeight, ScaleMode scaleMode) {
        double scale;
        Size size = new Size();
        if (ScaleMode.AspectFit == scaleMode) {
            if (viewHeight * imageWidth < viewWidth * imageHeight) {
                scale = 1.0 * viewHeight / imageHeight;
                size.width = (int) (scale * imageWidth + 0.5);
                size.height = viewHeight;
                size.x = (viewWidth - size.width) / 2;
                size.y = 0;
            } else {
                scale = 1.0 * viewWidth / imageWidth;
                size.width = viewWidth;
                size.height = (int) (scale * imageHeight + 0.5);
                size.x = 0;
                size.y = (viewHeight - size.height) / 2;
            }
        } else if (ScaleMode.CenterCrop == scaleMode) {
            if (viewHeight * imageWidth < viewWidth * imageHeight) {
                scale = 1.0 * viewWidth / imageWidth;
                size.width = viewWidth;
                size.height = (int) (scale * imageHeight + 0.5);
                size.x = 0;
                size.y = (viewHeight - size.height) / 2;
            } else {
                scale = 1.0 * viewHeight / imageHeight;
                size.width = (int) (scale * imageWidth + 0.5);
                size.height = viewHeight;
                size.x = (viewWidth - size.width) / 2;
                size.y = 0;
            }
        } else if (ScaleMode.Fill == scaleMode) {
            size.height = viewHeight;
            size.width = viewWidth;
            size.x = 0;
            size.y = 0;
        }
        return size;
    }


}
