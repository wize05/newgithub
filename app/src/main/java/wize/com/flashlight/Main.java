package wize.com.flashlight;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

@SuppressWarnings("deprecation")
public class Main extends Activity {

    ImageButton btnSwitch;

    private Camera camera;
    private boolean isFlashOn;
    private boolean hasFlash;
    Parameters params;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.flashlight_main);

        //Кнопка включения фонарика:
        btnSwitch = (ImageButton) findViewById(R.id.btnSwitch);

        //Проверяем поддержку работы с фонариком на устройстве:
        hasFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

        if (!hasFlash) {
            //Если вспышка не поддерживается, показываем
            //диалоговое окно с ошибкой и закрываем приложение:
            AlertDialog alert = new AlertDialog.Builder(Main.this)
                    .create();
            alert.setTitle("Ошибка");
            alert.setMessage("Ваше устройство не поддерживает работу с вспышкой!");
            alert.setButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    //Закрываем приложение:
                    finish();
                }
            });
            alert.show();
            return;
        }

        getCamera();

        //Отображаем переключатель:
        toggleButtonImage();

        //Обработчик события нажатий по кнопке:
        btnSwitch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isFlashOn) {
                    //Выключаем фонарик:
                    turnOffFlash();
                } else {
                    //Включаем фонарик:
                    turnOnFlash();
                }
            }
        });
    }
    //Получаем параметры камеры:
    private void getCamera() {
        if (camera == null) {
            try {
                camera = Camera.open();
                params = camera.getParameters();
            } catch (RuntimeException e) {
                Log.e("Ошибка!!!", e.getMessage());
            }
        }
    }
    //Включаем вспышку
    private void turnOnFlash() {
        if (!isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            //Проигрываем звук
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_TORCH);
            camera.setParameters(params);
            camera.startPreview();
            isFlashOn = true;

            //Меняем изображение кнопки при переключении:
            toggleButtonImage();
        }

    }
    //Выключаем фонарик
    private void turnOffFlash() {
        if (isFlashOn) {
            if (camera == null || params == null) {
                return;
            }
            //Проигрываем звук
            playSound();

            params = camera.getParameters();
            params.setFlashMode(Parameters.FLASH_MODE_OFF);
            camera.setParameters(params);
            camera.stopPreview();
            isFlashOn = false;

            //Меняем картинку кнопки:
            toggleButtonImage();
        }
    }
    //Проигрывание звука
    private void playSound(){
        if(isFlashOn){
            mp = MediaPlayer.create(Main.this, R.raw.sound);
        }else{
            mp = MediaPlayer.create(Main.this, R.raw.sound);
        }
        mp.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.release();
            }
        });
        mp.start();
    }
    /*
     Переключатель смены изображения
    */
    private void toggleButtonImage(){
        if(isFlashOn){
            btnSwitch.setImageResource(R.drawable.turn_on);
        }else{
            btnSwitch.setImageResource(R.drawable.turn_off);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();

        //Временно выключаем фонарик:
        turnOffFlash();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Продолжаем работу фонарика:
        if(hasFlash)
            turnOnFlash();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Получаем для приложения параметры камеры:
        getCamera();
    }

    @Override
    protected void onStop() {
        super.onStop();

        //Закрываем работу камеры:
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }
}