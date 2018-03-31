package ll.leon.com.opengladvance.ziptexture;

//状态更改侦听器
public interface StateChangeListener {

    int START = 1;
    int STOP = 2;
    int PLAYING = 3;

    void onStateChanged(int lastState, int nowState);

}
