package physics.collision;

import javafx.event.EventHandler;
import FX.calculator.time;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
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
    protected long lastCollisionTime;

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
        lastCollisionTime = System.currentTimeMillis();
    }

    public boolean collides(Sprite other) {
        return (this.x >= other.x && this.x <= other.x + other.w ||
                other.x >= this.x && other.x <= this.x + this.w) &&
                (other.y <= this.y && this.y <= other.y + other.h || this.y + this.h >= other.y && other.y >= this.y);
    }

    public void move(Sprite[] sprites) {
        x += v[0];
        y += v[1];

        v[0] += a[0];
        v[1] += a[1];

        if (x <= 0 || x + w >= width) {
            v[0] = -v[0] + a[0];
        }
        if (y <= 0 || y + h >= height - 60) {
            v[1] = -v[1] + a[1];
        }

        long time = System.currentTimeMillis();
        for (Sprite sprite : sprites) {
            if (time - this.lastCollisionTime > 10 && time - sprite.lastCollisionTime > 10) {
                if (this.this_id != sprite.this_id) {
                    if ((this.v[0] != 0 || this.v[1] != 0) && this.collides(sprite)) {
                        double new_speedX = (this.m - sprite.m) * this.v[0] / (this.m + sprite.m)
                                + (2 * sprite.m) * sprite.v[0] / (this.m + sprite.m);
                        double new_speedY = (this.m - sprite.m) * this.v[1] / (this.m + sprite.m)
                                + (2 * sprite.m) * sprite.v[1] / (this.m + sprite.m);

                        double new_sprite_speedX = (sprite.m - this.m) * sprite.v[0] / (this.m + sprite.m)
                                + (2 * this.m) * this.v[0] / (this.m + sprite.m);
                        double new_sprite_speedY = (sprite.m - this.m) * sprite.v[1] / (this.m + sprite.m)
                                + (2 * this.m) * this.v[1] / (this.m + sprite.m);

                        this.v[0] = new_speedX;
                        this.v[1] = new_speedY;
                        sprite.v[0] = new_sprite_speedX;
                        sprite.v[1] = new_sprite_speedY;

                        this.lastCollisionTime = System.currentTimeMillis();
                        sprite.lastCollisionTime = System.currentTimeMillis();
                    }
                }
            }
        }

        this.setLayoutX(x);
        this.setLayoutY(y);
    }
}

public class collision extends Application {
    private Sprite sprite1;
    private Sprite sprite2;
    public double width;
    public double height;
    private Sprite[] sprites;
    private double[] RClicked = new double[] { -1, -1 };
    private double[] LClicked = new double[] { -1, -1 };
    private boolean is_shifted = false;
    private boolean is_controled = false;
    private boolean is_spaced = false;

    public void start(Stage stage) {

        Pane root = new Pane();
        Scene scene = new Scene(root);

        stage.setTitle("Animation");
        stage.setScene(scene);
        stage.setMaximized(true);

        Rectangle2D rect = Screen.getPrimary().getBounds();

        width = rect.getWidth();
        height = rect.getHeight();

        sprite1 = new Sprite(width / 2 - 500, height / 2, 100, 100, Color.RED, new double[] { 0, 0 },
                new double[] { 0, 0 }, 50, width, height);
        sprite2 = new Sprite(width / 2 + 500, height / 2, 100, 100, Color.BLUE, new double[] { 0, 0 },
                new double[] { 0, 0 }, 100, width, height);

        sprites = new Sprite[] { sprite1, sprite2 };
        root.getChildren().addAll(sprites);

        stage.show();

        EventHandler<ScrollEvent> WheelScroll = new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double delta = event.getDeltaY() / 100;
                if (is_controled) {
                    if (is_spaced) {
                        sprite2.v[1] += (sprite2.v[1] < 0 ? -1 : 1) * delta;
                    } else {
                        sprite2.v[0] += (sprite2.v[0] < 0 ? -1 : 1) * delta;
                    }
                }
                if (!is_controled) {
                    if (is_spaced) {
                        sprite1.v[1] += (sprite1.v[1] < 0 ? -1 : 1) * delta;
                    } else {
                        sprite1.v[0] += (sprite1.v[0] < 0 ? -1 : 1) * delta;
                    }
                }
            }
        };

        EventHandler<MouseEvent> LMBClick = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                // TODO: acceleration control (ctrl to switch)
                if (event.getButton() == MouseButton.PRIMARY) {
                    LClicked[0] = event.getX();
                    LClicked[1] = event.getY();
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    RClicked[0] = event.getX();
                    RClicked[1] = event.getY();
                }
            }
        };

        EventHandler<MouseEvent> LMBRelease = new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton() == MouseButton.PRIMARY) {
                    if (LClicked[0] != -1 && LClicked[1] != -1) {
                        sprite1.x = LClicked[0];
                        sprite1.y = LClicked[1];
                        double dx = event.getX() - LClicked[0];
                        double dy = event.getY() - LClicked[1];
                        if (is_shifted) {
                            if (Math.abs(dx) > Math.abs(dy)) {
                                dy = 0;
                            } else {
                                dx = 0;
                            }
                        }
                        sprite1.v[0] = dx / 100;
                        sprite1.v[1] = dy / 100;
                    }
                } else if (event.getButton() == MouseButton.SECONDARY) {
                    if (RClicked[0] != -1 && RClicked[1] != -1) {
                        sprite2.x = RClicked[0];
                        sprite2.y = RClicked[1];
                        double dx = event.getX() - RClicked[0];
                        double dy = event.getY() - RClicked[1];
                        if (is_shifted) {
                            if (Math.abs(dx) > Math.abs(dy)) {
                                dy = 0;
                            } else {
                                dx = 0;
                            }
                        }
                        sprite2.v[0] = dx / 100;
                        sprite2.v[1] = dy / 100;
                    }
                }
            }
        };

        EventHandler<KeyEvent> keyPressed = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.SHIFT) {
                    is_shifted = true;
                }
                if (event.getCode() == KeyCode.CONTROL) {
                    is_controled = true;
                }
                if (event.getCode() == KeyCode.SPACE) {
                    is_spaced = true;
                }
                if (event.getCode() == KeyCode.RIGHT) {
                    if (is_controled) {
                        sprite2.x += 5;
                    } else {
                        sprite1.x += 5;
                    }
                }
                if (event.getCode() == KeyCode.LEFT) {
                    if (is_controled) {
                        sprite2.x -= 5;
                    } else {
                        sprite1.x -= 5;
                    }
                }
                if (event.getCode() == KeyCode.UP) {
                    if (is_controled) {
                        sprite2.y -= 5;
                    } else {
                        sprite1.y -= 5;
                    }
                }
                if (event.getCode() == KeyCode.DOWN) {
                    if (is_controled) {
                        sprite2.y += 5;
                    } else {
                        sprite1.y += 5;
                    }
                }
            }
        };

        EventHandler<KeyEvent> keyReleased = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.SHIFT) {
                    is_shifted = false;
                } else if (event.getCode() == KeyCode.CONTROL) {
                    is_controled = false;
                } else if (event.getCode() == KeyCode.SPACE) {
                    is_spaced = false;
                }
            }
        };

        ChangeListener<Number> StageWidthChanged = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number old_v, Number new_v) {
                sprite1.width = (double) new_v;
                sprite2.width = (double) new_v;
            }
        };

        ChangeListener<Number> StageHeightChanged = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number old_v, Number new_v) {
                sprite1.height = (double) new_v;
                sprite2.height = (double) new_v;
            }
        };

        stage.widthProperty().addListener(StageWidthChanged);
        stage.heightProperty().addListener(StageHeightChanged);

        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, LMBClick);
        scene.addEventHandler(MouseEvent.MOUSE_RELEASED, LMBRelease);
        scene.addEventHandler(ScrollEvent.SCROLL, WheelScroll);
        scene.setOnKeyPressed(keyPressed);
        scene.setOnKeyReleased(keyReleased);

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
