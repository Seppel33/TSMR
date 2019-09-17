package gui;


import engine.ShapesRendererPP;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import simulation.PhysicsThreadManager;

import java.io.File;

public class TwoDUI extends AnchorPane {
    private Label[] marbleLabelTop;
    private Label[] marbleLabelBottom;
    private ImageView[][][] imageViews;
    private Image[][][] images;
    private int[] dimensions;
    private static int[][] levelData;
    private GridPane[] gridPane = new GridPane[2];

    private Text captionTop;
    private Text captionBottom;

    private boolean onMarble;
    private Image[] marbleImages;
    private int whichMarble;
    double tileWidth;
    double tileHeight;
    double tileLength;
    PhysicsThreadManager physics;
    private int[] marbleSectorPosition;
    public TwoDUI(PhysicsThreadManager physics){
        this.physics = physics;
        whichMarble=0;
        onMarble=true;
        gridPane[0] = new GridPane();
        gridPane[1] = new GridPane();

        Screen screen = Screen.getPrimary();
        levelData=ShapesRendererPP.getLevelData();

        this.dimensions = levelData[0];

        //größte dimensions nehmen damit array groß genug ist
        imageViews=new ImageView[2][][];
        imageViews[0]=new ImageView[dimensions[0]][dimensions[2]];
        imageViews[1]=new ImageView[dimensions[0]][dimensions[1]];


        Label[][][] label = new Label[2][6][6];
        images=new Image[2][11][4];
        double width=screen.getVisualBounds().getWidth()*0.8;
        double height=screen.getVisualBounds().getHeight()*0.8;
        this.setPrefSize(width/4,height);


        tileWidth = (width)/5 /dimensions[0];
        tileHeight = (height)/3/dimensions[1];
        tileLength = (height)/3/dimensions[2];
        double shortestValue=tileWidth;
        if(tileHeight<tileWidth)shortestValue=tileHeight;
        if(tileLength<shortestValue)shortestValue=tileLength;


        for (int blockCounter = 0; blockCounter < images[0].length; blockCounter++) {
            for (int sideCounter = 0; sideCounter < images[0][0].length; sideCounter++) {
                images[0][blockCounter][sideCounter] = new Image(new File("res\\2DTiles\\TopView\\" + blockCounter + "_" + (sideCounter % 6) + ".png").toURI().toString(), shortestValue, shortestValue, true, false);
            }
        }
        images[0][imageViews[0].length-1][0] = new Image(new File("res\\2DTiles\\x.png").toURI().toString(), shortestValue, shortestValue, true, false);

        for (int blockCounter = 0; blockCounter < images[0].length; blockCounter++) {
            for (int sideCounter = 0; sideCounter < images[0][0].length; sideCounter++) {
                images[1][blockCounter][sideCounter] = new Image(new File("res\\2DTiles\\FrontView\\" + blockCounter + "_" + (sideCounter % 6) + ".png").toURI().toString(), shortestValue, shortestValue, true, false);
            }
        }
        images[1][imageViews[0].length-1][0] = new Image(new File("res\\2DTiles\\x.png").toURI().toString(), shortestValue, shortestValue, true, false);


        for (int i = 0; i < dimensions[0]; i++) {
            for (int j = 0; j < dimensions[2]; j++) {
                imageViews[0][i][j] = new ImageView();
                label[0][i][j] = new Label();
                label[0][i][j].setGraphic(imageViews[0][i][j]);
                gridPane[0].add(label[0][i][j], i, j);
            }
        }
        for (int i = 0; i < dimensions[0]; i++) {
            for (int j = 0; j < dimensions[1]; j++) {
                imageViews[1][i][j] = new ImageView();
                label[1][i][j] = new Label();
                label[1][i][j].setGraphic(imageViews[1][i][j]);
                gridPane[1].add(label[1][i][j], i, j);
            }
        }
        this.getChildren().add(gridPane[0]);
        this.getChildren().add(gridPane[1]);


        File layer_up = new File("res\\layer_up.png");
        Image image3 = new Image(layer_up.toURI().toString());
        Button topLayerUp = new Button();
        topLayerUp.setPrefSize(40, 40);
        topLayerUp.setGraphic(new ImageView(image3));
        topLayerUp.setId("btnInGame");
        this.getChildren().add(topLayerUp);



        File layer_down = new File("res\\layer_down.png");
        Image image4 = new Image(layer_down.toURI().toString());
        Button topLayerDown = new Button();
        topLayerDown.setPrefSize(40, 40);
        topLayerDown.setGraphic(new ImageView(image4));
        topLayerDown.setId("btnInGame");
        this.getChildren().add(topLayerDown);


        File nav = new File("res\\nav.png");

        Image image5 = new Image(nav.toURI().toString());
        Button backToMarbleButton = new Button();
        backToMarbleButton.setPrefSize(40, 40);
        backToMarbleButton.setGraphic(new ImageView(image5));
        backToMarbleButton.setId("btnInGame");
        this.getChildren().add(backToMarbleButton);


        topLayerUp.setOnAction(actionEvent -> {

            onMarble=false;
            int[] marbleSectorPos=getMarbleSectorPosition().clone();
            marbleSectorPos[1]+=1;

            if(marbleSectorPos[1]>dimensions[1]-1)marbleSectorPos[1]=dimensions[1]-1;
            setMarbleSectorPosition(marbleSectorPos);
            changeLayer(marbleSectorPos,true);


            marbleLabelTop[whichMarble].setGraphic(new ImageView(marbleImages[1]));
            marbleLabelBottom[whichMarble].setGraphic(new ImageView(marbleImages[1]));
        });

        topLayerDown.setOnAction(actionEvent -> {

            onMarble=false;
            int[] marbleSectorPos=getMarbleSectorPosition().clone();
            marbleSectorPos[1]-=1;


            if(marbleSectorPos[1]<0)marbleSectorPos[1]=0;
            setMarbleSectorPosition(marbleSectorPos);
            changeLayer(marbleSectorPos,true);


            marbleLabelTop[whichMarble].setGraphic(new ImageView(marbleImages[1]));
            marbleLabelBottom[whichMarble].setGraphic(new ImageView(marbleImages[1]));
        });

        backToMarbleButton.setOnAction(actionEvent -> {
            onMarble=true;
            marbleLabelTop[whichMarble].setGraphic(new ImageView(marbleImages[1]));
            marbleLabelBottom[whichMarble].setGraphic(new ImageView(marbleImages[1]));
            changeLayer(ShapesRendererPP.getMarbleSectorPosition()[whichMarble],true);
            if((whichMarble+1>= PhysicsThreadManager.getMarbles().length)){
                whichMarble=0;
            }else{
                whichMarble++;
            }
            marbleLabelTop[whichMarble].setGraphic(new ImageView(marbleImages[0]));
            marbleLabelBottom[whichMarble].setGraphic(new ImageView(marbleImages[0]));


        });


        File layer_up2 = new File("res\\layer_up.png");
        Image image6 = new Image(layer_up2.toURI().toString());
        Button bottomLayerUp = new Button();
        bottomLayerUp.setPrefSize(40, 40);
        bottomLayerUp.setGraphic(new ImageView(image6));
        bottomLayerUp.setId("btnInGame");
        this.getChildren().add(bottomLayerUp);


        File layer_down2 = new File("res\\layer_down.png");
        Image image7 = new Image(layer_down2.toURI().toString());
        Button bottomLayerDown = new Button();
        bottomLayerDown.setPrefSize(40, 40);
        bottomLayerDown.setGraphic(new ImageView(image7));
        bottomLayerDown.setId("btnInGame");
        this.getChildren().add(bottomLayerDown);



        bottomLayerUp.setOnAction(actionEvent -> {
            onMarble=false;
            int[] marbleSectorPos=getMarbleSectorPosition().clone();
            marbleSectorPos[2]+=1;

            if(marbleSectorPos[2]>dimensions[2]-1)marbleSectorPos[2]=dimensions[2]-1;
            setMarbleSectorPosition(marbleSectorPos);
            changeLayer(marbleSectorPos,true);


            marbleLabelTop[whichMarble].setGraphic(new ImageView(marbleImages[1]));
            marbleLabelBottom[whichMarble].setGraphic(new ImageView(marbleImages[1]));
        });

        bottomLayerDown.setOnAction(actionEvent -> {
            onMarble=false;
            int[] marbleSectorPos=getMarbleSectorPosition().clone();
            marbleSectorPos[2]-=1;


            if(marbleSectorPos[2]<0)marbleSectorPos[2]=0;
            setMarbleSectorPosition(marbleSectorPos);
            changeLayer(marbleSectorPos,true);


            marbleLabelTop[whichMarble].setGraphic(new ImageView(marbleImages[1]));
            marbleLabelBottom[whichMarble].setGraphic(new ImageView(marbleImages[1]));
        });

        captionTop = new Text("Top View");
        captionTop.setFill(new Color(0.9,0.9,0.9,1));
        captionTop.setFont(new Font("Avenir",25));
        captionTop.setId("topView");
        this.getChildren().add(captionTop);

        captionBottom = new Text("Front View");
        captionBottom.setFill(new Color(0.9,0.9,0.9,1));
        captionBottom.setFont(new Font("Avenir",25));
        captionBottom.setId("botView");
        this.getChildren().add(captionBottom);



        double leftAforGrid=0.75;
        double rightAforButtons=0.03;
        AnchorPane.setLeftAnchor(captionTop,width*leftAforGrid);
        AnchorPane.setLeftAnchor(captionBottom,width*leftAforGrid);

        AnchorPane.setTopAnchor(captionTop,height*0.05);
        AnchorPane.setTopAnchor(captionBottom,height*0.55);

        AnchorPane.setLeftAnchor(gridPane[0],width*leftAforGrid);
        AnchorPane.setLeftAnchor(gridPane[1],width*leftAforGrid);
        AnchorPane.setTopAnchor(gridPane[0],height*0.1);
        AnchorPane.setTopAnchor(gridPane[1],height*0.6);



        AnchorPane.setTopAnchor(topLayerUp,  height *0.15);
        AnchorPane.setRightAnchor(topLayerUp, width*rightAforButtons);
        AnchorPane.setTopAnchor(topLayerDown, height*0.25);
        AnchorPane.setRightAnchor(topLayerDown, width*rightAforButtons);
        AnchorPane.setTopAnchor(backToMarbleButton, height * 0.45);

        AnchorPane.setRightAnchor(backToMarbleButton, width*rightAforButtons);

        AnchorPane.setTopAnchor(bottomLayerUp,  height*0.65);
        AnchorPane.setRightAnchor(bottomLayerUp,  width*rightAforButtons);
        AnchorPane.setTopAnchor(bottomLayerDown,  height*0.75);
        AnchorPane.setRightAnchor(bottomLayerDown,  width*rightAforButtons);

        marbleImages=new Image[2];
        marbleImages[0]=new Image(new File("res\\2DTiles\\marble0.png").toURI().toString(), (tileWidth / 5) * 1.48, (tileHeight / 5) * 1.48, true, false);
        marbleImages[1]=new Image(new File("res\\2DTiles\\marble1.png").toURI().toString(), (tileWidth / 5) * 1.48, (tileHeight / 5) * 1.48, true, false);

        marbleLabelTop =new Label[PhysicsThreadManager.getMarbles().length];
        marbleLabelBottom = new Label[PhysicsThreadManager.getMarbles().length];

        for(int marbleCounter = 0; marbleCounter< PhysicsThreadManager.getMarbles().length; marbleCounter++) {
            marbleLabelTop[marbleCounter]=new Label();
            marbleLabelBottom[marbleCounter]= new Label();
            if(marbleCounter==0){
                marbleLabelTop[marbleCounter].setGraphic(new ImageView(marbleImages[0]));
                this.getChildren().add(marbleLabelTop[marbleCounter]);

                marbleLabelBottom[marbleCounter].setGraphic(new ImageView(marbleImages[0]));
                this.getChildren().add(marbleLabelBottom[marbleCounter]);
            }else {
                marbleLabelTop[marbleCounter].setGraphic(new ImageView(marbleImages[1]));
                this.getChildren().add(marbleLabelTop[marbleCounter]);

                marbleLabelBottom[marbleCounter].setGraphic(new ImageView(marbleImages[1]));
                this.getChildren().add(marbleLabelBottom[marbleCounter]);
            }

        }

    }


    public void changeLayer(int[] marbleSectorPosition,boolean fromButton) {

        if (onMarble||fromButton) {
            setMarbleSectorPosition(marbleSectorPosition);
            levelData = ShapesRendererPP.getLevelData();

            Platform.runLater(() -> {
                //javaFX operations should go here
                captionTop.setText("Top View      " + (dimensions[1] - marbleSectorPosition[1]) + " / " + dimensions[1]);
            });


            for (int lengthCounter = 0; lengthCounter < dimensions[2]; lengthCounter++) {
                for (int widthCounter = 0; widthCounter < dimensions[0]; widthCounter++) {
                    int[] sectorData = levelData[dimensions[0] * dimensions[2] * marbleSectorPosition[1] + 1 + widthCounter + lengthCounter * dimensions[0]];
                    if (sectorData.length == 2) {
                        imageViews[0][widthCounter][lengthCounter].setImage(images[0][sectorData[0]][sectorData[1]]);//[sectorData[1] % 6]);

                    } else {
                        imageViews[0][widthCounter][lengthCounter].setImage(images[0][imageViews[0].length-1][0]);
                    }

                }
            }
            Platform.runLater(() -> {
                //javaFX operations should go here
                captionBottom.setText("Front View      " + (dimensions[2] - marbleSectorPosition[2]) + " / " + dimensions[2]);
            });
            for (int heightCounter = 0; heightCounter < dimensions[1]; heightCounter++) {
                for (int widthCounter = 0; widthCounter < dimensions[0]; widthCounter++) {
                    int[] sectorData = levelData[widthCounter + heightCounter * dimensions[0] * dimensions[2] + 1 + marbleSectorPosition[2] * dimensions[0]];
                    if (sectorData.length == 2) {

                        imageViews[1][widthCounter][dimensions[1] - 1 - heightCounter].setImage(images[1][sectorData[0]][sectorData[1] % 6]);
                    } else {
                        imageViews[1][widthCounter][dimensions[1] - 1 - heightCounter].setImage(images[1][imageViews[0].length-1][0]);
                    }
                }
            }
        }
    }

    public void changeMarblePosition(double[][] marble){
        double minXT=gridPane[0].boundsInParentProperty().getValue().getMinX();
        double maxXT=gridPane[0].boundsInParentProperty().getValue().getMaxX();
        double minYT=gridPane[0].boundsInParentProperty().getValue().getMinY();
        double maxYT=gridPane[0].boundsInParentProperty().getValue().getMaxY();

        double minXB=gridPane[1].boundsInParentProperty().getValue().getMinX();
        double maxXB=gridPane[1].boundsInParentProperty().getValue().getMaxX();
        double minYB=gridPane[1].boundsInParentProperty().getValue().getMinY();
        double maxYB=gridPane[1].boundsInParentProperty().getValue().getMaxY();

        double radiusOfMarbleTextur=(tileWidth/5)*(1.7/2);


        for(int marbleCounter = 0; marbleCounter<PhysicsThreadManager.getMarbles().length;marbleCounter++) {
            double topTopAnchorPos = (minYT + (marble[marbleCounter][2] / dimensions[2]) * (maxYT - minYT)) - radiusOfMarbleTextur;
            double topLeftAnchorPos = (minXT + (marble[marbleCounter][0] / dimensions[0]) * (maxXT - minXT)) - radiusOfMarbleTextur;
            double x =(minYB + ((marble[marbleCounter][1]) / dimensions[1]) * (maxYB - minYB)) ;
            double frontTopAnchorPos = maxYB-x+minYB- radiusOfMarbleTextur;
            double frontLeftAnchorPos = (minXB + (marble[marbleCounter][0] / dimensions[0]) * (maxXB - minXB)) - radiusOfMarbleTextur;
            if (topTopAnchorPos > maxYT) topTopAnchorPos = maxYT - radiusOfMarbleTextur;
            if (topTopAnchorPos < minYT) topTopAnchorPos = minYT - radiusOfMarbleTextur;
            if (topLeftAnchorPos > maxXT) topLeftAnchorPos = maxXT - radiusOfMarbleTextur;
            if (topLeftAnchorPos < minXT) topLeftAnchorPos = minXT - radiusOfMarbleTextur;

            if (frontTopAnchorPos > maxYB) frontTopAnchorPos = maxYB - radiusOfMarbleTextur;
            if (frontTopAnchorPos < minYB) frontTopAnchorPos = minYB - radiusOfMarbleTextur;
            if (frontLeftAnchorPos > maxXB) frontLeftAnchorPos = maxXB - radiusOfMarbleTextur;
            if (frontLeftAnchorPos < minXB) frontLeftAnchorPos = minXB - radiusOfMarbleTextur;


            AnchorPane.setTopAnchor(marbleLabelTop[marbleCounter], topTopAnchorPos);
            AnchorPane.setLeftAnchor(marbleLabelTop[marbleCounter], topLeftAnchorPos);

            AnchorPane.setTopAnchor(marbleLabelBottom[marbleCounter], frontTopAnchorPos);
            AnchorPane.setLeftAnchor(marbleLabelBottom[marbleCounter], frontLeftAnchorPos);
        }
    }

    public int[] getMarbleSectorPosition() {
        return marbleSectorPosition;
    }

    public void setMarbleSectorPosition(int[] marbleSectorPosition) {
        this.marbleSectorPosition = marbleSectorPosition;
    }

    public int getWhichMarble() {
        return whichMarble;
    }
    public boolean isOnMarble() {
        return onMarble;
    }
}
