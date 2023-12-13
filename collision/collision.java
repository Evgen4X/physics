package physics.collision;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;

class Sprite extends Rectangle {
    public double[] v, a;
    public double m;
    public double x, y, w, h;
    public double width, height;
    private static int id = 0;
    protected int this_id;

    public Sprite(double x, double y, double w, double h, Color fill_color, double[] v, double[] a, double m,
            double max_width, double max_height) {
        width = max_width;
        height = max_height;
        this.this_id = id++;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.v = v;
        this.a = a;
        this.m = m;
        this.setLayoutX(x);
        this.setLayoutY(y);
        this.setWidth(w);
        this.setHeight(h);
        this.setFill(fill_color);

        System.out.println(this.v[0]);
        System.out.println(this.v[1]);

    }

    public boolean collidesX(Sprite other) {
        return (this.x >= other.x && this.x <= other.x + other.w ||
                other.x >= this.x && other.x <= this.x + this.w) &&
                (other.y <= this.y && this.y <= other.y + other.h || this.y + this.h >= other.y && other.y >= this.y);
    }

    public boolean collidesY(Sprite other) {
        return (this.y >= other.y && this.y <= other.y + other.h ||
                other.y >= this.y && other.y <= this.y + this.h) &&
                (other.x <= this.x && this.x <= other.x + other.w || this.x + this.w >= other.x && other.x >= this.x);
    }

    public void move(Sprite[] sprites) {
        x += v[0];
        y += v[1];

        v[0] += a[0];
        v[1] += a[1];

        if (x <= 0 || x + w >= width) {
            v[0] = -v[0];
            a[0] = -a[0];
        }
        if (y <= 0 || y + h >= height - 50) {
            v[1] = -v[1];
            a[1] = -a[1];
        }

        for (Sprite sprite : sprites) {
            if (this.this_id != sprite.this_id) {
                if (this.v[0] != 0 && this.collidesX(sprite)) {
                    double new_speed = (this.m - sprite.m) * this.v[0] / (this.m + sprite.m)
                            + (2 * sprite.m) * sprite.v[0] / (this.m + sprite.m);

                    double new_sprite_speed = (sprite.m - this.m) * sprite.v[0] / (this.m + sprite.m)
                            + (2 * this.m) * this.v[0] / (this.m + sprite.m);

                    if (this.v[0] * new_speed < 0) {
                        this.a[0] = -this.a[0];
                    }
                    this.v[0] = new_speed;

                    if (this.v[0] * new_sprite_speed < 0) {
                        sprite.a[0] = -sprite.a[0];
                    }
                    sprite.v[0] = new_sprite_speed;
                } else if (this.v[1] != 0 && this.collidesY(sprite)) {
                    double new_speed = (this.m - sprite.m) * this.v[1] / (this.m + sprite.m)
                            + (2 * sprite.m) * sprite.v[1] / (this.m + sprite.m);

                    double new_sprite_speed = (sprite.m - this.m) * sprite.v[1] / (this.m + sprite.m)
                            + (2 * this.m) * this.v[1] / (this.m + sprite.m);

                    if (this.v[1] * new_speed < 1) {
                        this.a[1] = -this.a[1];
                    }
                    this.v[1] = new_speed;

                    if (this.v[1] * new_sprite_speed < 0) {
                        sprite.a[1] = -sprite.a[1];
                    }
                    sprite.v[1] = new_sprite_speed;
                }
            }
        }

        if (Math.abs(v[0]) <= 0.02) {
            a[0] = 0;
            v[0] = 0;
        }
        if (Math.abs(v[1]) <= 0.02) {
            a[1] = 0;
            v[1] = 0;
        }

        this.setLayoutX(x);
        this.setLayoutY(y);
        // System.out.printf("(%f, %f)\n", this.x, this.y);
    }
}

public class collision extends Application {
    private Sprite sprite1;
    private Sprite sprite2;
    public double width;
    public double height;
    private Sprite[] sprites;

    public void start(Stage stage) {

        Pane root = new Pane();
        Scene scene = new Scene(root);

        stage.setTitle("Animation");
        stage.setScene(scene);
        stage.setMaximized(true);

        Rectangle2D rect = Screen.getPrimary().getBounds();

        width = rect.getWidth();
        height = rect.getHeight();

        sprite1 = new Sprite(width / 2 - 500, height / 2, 100, 100, Color.RED, new double[] { 0, 2 },
                new double[] { 0, 0 }, 50, width, height);
        sprite2 = new Sprite(width / 2 + 500, height / 2, 100, 100, Color.BLUE, new double[] { -3, 0 },
                new double[] { 0, 0 }, 100, width, height);

        sprites = new Sprite[] { sprite1, sprite2 };
        root.getChildren().addAll(sprites);

        stage.show();

        new AnimationTimer() {
            @Override
            public void handle(long now) {
                loop();
            }
        }.start();
    }

    public void loop() {
        sprite1.move(sprites);
        sprite2.move(sprites);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
