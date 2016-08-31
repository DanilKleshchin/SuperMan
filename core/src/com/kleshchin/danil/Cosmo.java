package com.kleshchin.danil;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;


import java.util.Iterator;

import javafx.scene.input.TouchEvent;
import javafx.stage.Screen;
import sun.java2d.ScreenUpdateManager;


public class Cosmo extends ApplicationAdapter {
    Preferences prefs;

    OrthographicCamera camera;
    SpriteBatch batch;


    Texture planImage;
    Texture enemyImage;
    Texture stoneImage;
    Texture gunImage;
    Texture heartImage;
    Texture bossImage;

	/*Sound jumpSound;
    Sound damageSound;*/
	Music backgroundMusic;

    Vector3 touchPos;

    Rectangle plan;
    Array<Rectangle> stone;
    Array<Rectangle> enemy;
    Array<Rectangle> gun;
    Array<Rectangle> heart;
    Array<Rectangle> boss;

    private long lastStoneTime;
    private long lastEnemyTime;
    private long lastGunTime;
    private long lastHeartTime;
    private long lastBossTime;

    private String scoreName;
    BitmapFont bitmapFontName;

    private String recordName;
    BitmapFont recordBitmapFontName;

    private String lifeName;
    BitmapFont lifeBitmapFontName;

    public static Texture backgroundTexture;
    public static Sprite backgroundSprite;

    private int score;
    private int record;
    private int destroy;
    private int destroyBoss;
    private int life;


    @Override
    public void create() {
        backgroundTexture = new Texture("background.png");
        backgroundSprite = new Sprite(backgroundTexture);
        prefs = Gdx.app.getPreferences("Cosmo");
        score = 0;
        destroy = 0;
        destroyBoss = 0;
        life = 5;

        if (prefs.getString("record", "").equals(""))
            record = 0;
        else {
            record = Integer.parseInt(prefs.getString("record", ""));
        }
        recordName = "Record: " + record;
        lifeName = "Life: " + life;
        scoreName = "Score: " + score;
        bitmapFontName = new BitmapFont();
        recordBitmapFontName = new BitmapFont();
        lifeBitmapFontName = new BitmapFont();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);
        touchPos = new Vector3();
        batch = new SpriteBatch();
        planImage = new Texture("plan.png");
        stoneImage = new Texture("stone.png");
        enemyImage = new Texture("enemy.png");
        gunImage = new Texture("gun.png");
        heartImage = new Texture("heart.png");
        bossImage = new Texture("boss.png");

		/*jumpSound = Gdx.audio.newSound(Gdx.files.internal(".wav"));			//TODO download sound
        damageSound = Gdx.audio.newSound(Gdx.files.internal(".wav"));	*/    //TODO download sound
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));

		backgroundMusic.setLooping(true);
        backgroundMusic.play();

        plan = new Rectangle();
        plan.x = 480 / 2 - 64 / 2;
        plan.y = 20;
        plan.width = 64;
        plan.height = 183;

        stone = new Array<Rectangle>();
        gun = new Array<Rectangle>();
        enemy = new Array<Rectangle>();
        heart = new Array<Rectangle>();
        boss = new Array<Rectangle>();

        spawnGun();
        spawnEnemy();
    }

    private void spawnBoss() {
        Rectangle bossDrop = new Rectangle();
        bossDrop.x = 480 / 2 - 256 / 2;
        bossDrop.y = 800;
        bossDrop.width = 256;
        bossDrop.height = 200;
        boss.add(bossDrop);
        lastBossTime = TimeUtils.millis();
    }

    private void spawnGun() {
        Rectangle gunShoot = new Rectangle();
        gunShoot.x = plan.getX() + 100 / 2;
        gunShoot.y = plan.getY() + 100;
        gunShoot.width = 13;
        gunShoot.height = 29;
        gun.add(gunShoot);
        lastGunTime = TimeUtils.millis();
    }

    private void spawnEnemy() {
        Rectangle enemyDrop = new Rectangle();
        enemyDrop.x = MathUtils.random(0, 480 - 64);
        enemyDrop.y = 800;
        enemyDrop.width = 32;
        enemyDrop.height = 64;
        enemy.add(enemyDrop);
        lastEnemyTime = TimeUtils.nanoTime();
    }

    private void spawnStone() {
        Rectangle stoneDrop = new Rectangle();
        stoneDrop.x = MathUtils.random(0, 480 - 64);
        stoneDrop.y = 800;
        stoneDrop.width = 256;
        stoneDrop.height = 256;
        stone.add(stoneDrop);
        lastStoneTime = TimeUtils.millis();
    }

    private void spawnHeart() {
        Rectangle heartDrop = new Rectangle();
        heartDrop.x = MathUtils.random(0, 480 - 64);
        heartDrop.y = 800;
        heartDrop.width = 64;
        heartDrop.height = 64;
        heart.add(heartDrop);
        lastHeartTime = TimeUtils.millis();
    }


    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();


        backgroundSprite.draw(batch);

        bitmapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        bitmapFontName.draw(batch, scoreName, 25, 300);
        recordBitmapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        recordBitmapFontName.draw(batch, recordName, 25, 400);
        lifeBitmapFontName.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        lifeBitmapFontName.draw(batch, lifeName, 25, 500);

        batch.draw(planImage, plan.x, plan.y);
        for (Rectangle enemydrop : enemy) {
            batch.draw(enemyImage, enemydrop.x, enemydrop.y);
        }
        for (Rectangle stonedrop : stone) {
            batch.draw(stoneImage, stonedrop.x, stonedrop.y);
        }
        for (Rectangle gundrop : gun) {
            batch.draw(gunImage, gundrop.x, gundrop.y);
        }
        for (Rectangle heartdrop : heart) {
            batch.draw(heartImage, heartdrop.x, heartdrop.y);
        }
        for (Rectangle bossdrop : boss) {
            batch.draw(bossImage, bossdrop.x, bossdrop.y);
        }


        batch.end();

        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);
            plan.x = touchPos.x - 128 / 2;
            plan.y = touchPos.y - 128 / 2;

        }

        if (TimeUtils.nanoTime() - lastEnemyTime > 1000000000)
            spawnEnemy();
        if (TimeUtils.millis() - lastGunTime > 500)
            spawnGun();
        if (score > 15) {
            if (TimeUtils.millis() - lastStoneTime > 5000)
                spawnStone();
        }
        if (score > 20) {
            if (TimeUtils.millis() - lastHeartTime > 10000)
                spawnHeart();
        }
        //if (score % 50 == 0) {
        if (score != 0 && score % 50 == 0) {
            if (TimeUtils.millis() - lastBossTime > 5000)
                spawnBoss();
        }


        Iterator<Rectangle> iteratorEnemy = enemy.iterator();
        while (iteratorEnemy.hasNext()) {
            Rectangle enemydrop = iteratorEnemy.next();
            Iterator<Rectangle> iteratorGun = gun.iterator();
            while (iteratorGun.hasNext()) {
                Rectangle gundrop = iteratorGun.next();
                if (enemydrop.overlaps(gundrop)) {
                    score++;
                    scoreName = "Score: " + score;
                    if (score > record) {
                        record = score;
                        recordName = "Record: " + Integer.toString(record);
                        prefs.putString("record", Integer.toString(record));
                        prefs.flush();
                    }
                    iteratorEnemy.remove();
                    iteratorGun.remove();
                } else {
                    gundrop.y += 200 * Gdx.graphics.getDeltaTime();
                    if (gundrop.y > 800) {
                        iteratorGun.remove();
                    }
                }
            }
            if (enemydrop.overlaps(plan)) {
                iteratorEnemy.remove();
                life--;
                lifeName = "Life: " + life;
                if (life <= 0) {
                    Gdx.gl.glClearColor(0, 0, 0.2f, 1);
                    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                    dispose();
                    create();
                }
            }
            enemydrop.y -= 100 * Gdx.graphics.getDeltaTime();
            if (enemydrop.y + 64 < 0) {
                iteratorEnemy.remove();
            }
        }


        Iterator<Rectangle> iteratorStone = stone.iterator();
        while (iteratorStone.hasNext()) {
            Rectangle stonedrop = iteratorStone.next();
            Iterator<Rectangle> iteratorGun = gun.iterator();
            while (iteratorGun.hasNext()) {
                Rectangle gundrop = iteratorGun.next();
                if (stonedrop.overlaps(gundrop)) {
                    destroy++;
                    if (destroy == 5) {
                        score += 3;
                        scoreName = "Score: " + score;
                        if (score > record) {
                            record = score;
                            recordName = "Record: " + Integer.toString(record);
                            prefs.putString("record", Integer.toString(record));
                            prefs.flush();

                        }
                        destroy = 0;
                        iteratorStone.remove();
                    }
                    iteratorGun.remove();
                } else {
                    gundrop.y += 200 * Gdx.graphics.getDeltaTime();
                    if (gundrop.y > 800) {
                        iteratorGun.remove();
                    }
                }
                if (stonedrop.overlaps(plan)) {
                    iteratorStone.remove();
                    life--;
                    lifeName = "Life: " + life;
                    if (life <= 0) {
                        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
                        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                        dispose();
                        create();
                    }
                }
            }
            stonedrop.y -= 100 * Gdx.graphics.getDeltaTime();
            if (stonedrop.y < 0) {
                iteratorStone.remove();
            }
        }


        Iterator<Rectangle> iteratorHeart = heart.iterator();
        while (iteratorHeart.hasNext()) {
            Rectangle heartdrop = iteratorHeart.next();
            heartdrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (heartdrop.y < 0) {
                iteratorHeart.remove();
            }
            if (heartdrop.overlaps(plan)) {
                life++;
                lifeName = "Life: " + life;
                iteratorHeart.remove();
            }
        }

        Iterator<Rectangle> iteratorBoss = boss.iterator();
        while (iteratorBoss.hasNext()) {
            Rectangle bossdrop = iteratorBoss.next();
            Iterator<Rectangle> iteratorGun = gun.iterator();
            while (iteratorGun.hasNext()) {
                Rectangle gundrop = iteratorGun.next();
                if (bossdrop.overlaps(gundrop)) {
                    destroyBoss++;
                    if (destroyBoss == 40) {
                        score += 50;
                        scoreName = "Score: " + score;
                        if (score > record) {
                            record = score;
                            recordName = "Record: " + Integer.toString(record);
                            prefs.putString("record", Integer.toString(record));
                            prefs.flush();

                        }
                        destroyBoss = 0;
                        iteratorBoss.remove();
                    }
                    iteratorGun.remove();
                } else {
                    gundrop.y += 200 * Gdx.graphics.getDeltaTime();
                    if (gundrop.y > 800) {
                        iteratorGun.remove();
                    }
                }
                if (bossdrop.overlaps(plan)) {
                    iteratorBoss.remove();
                    life -= 10;
                    lifeName = "Life: " + life;
                    if (life <= 0) {
                        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
                        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
                        dispose();
                        create();
                    }
                }
                bossdrop.y -= 30 * Gdx.graphics.getDeltaTime();
                if (bossdrop.y < 0) {
                    iteratorBoss.remove();
                }
               /* if (bossdrop.y < 100) {
                    bossdrop.y += 200 * Gdx.graphics.getDeltaTime();
                }*/

                //bossShootGun();
            }
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        planImage.dispose();
        enemyImage.dispose();
        bossImage.dispose();
        heartImage.dispose();
        stoneImage.dispose();
        gunImage.dispose();
        backgroundMusic.dispose();
        bitmapFontName.dispose();
        lifeBitmapFontName.dispose();
        recordBitmapFontName.dispose();
        recordBitmapFontName.dispose();
        backgroundTexture.dispose();
    }
}