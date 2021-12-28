package com.colliu.colliu.controllers;

import com.colliu.colliu.MasterController;
import event.Event;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

public class EventItem {
  double normalHeight;
  double detailsHeight;
  int eventId;
  boolean attending;
  Event event;
  SequentialTransition emojiAnimation;

  @FXML
  private Pane eventBackground;

  @FXML
  private ImageView imgBanner;

  @FXML
  private Label lblAttendees;

  @FXML
  private Label lblDate;

  @FXML
  private Label lblLocation;

  @FXML
  private Label lblTime;

  @FXML
  private Label lblTitle;

  @FXML
  private TextArea txtDescription;

  @FXML
  private Button btnShowDetails;

  @FXML
  private Pane pnEventDetails;

  @FXML
  private StackPane spAllEvent;

  @FXML
  private ToggleButton btnAttend;

  @FXML
  private ImageView imgReactionOne;

  @FXML
  private ImageView imgReactionTwo;

  @FXML
  private ImageView imgReactionThree;

  @FXML
  private ImageView imgReactionFour;

  @FXML
  private Label lblReactions;

  @FXML
  private VBox vbReactions;

  @FXML
  private Pane pnReactions;

  @FXML
  private StackPane stpReactions;

  @FXML
  private StackPane stpAttendees;

  @FXML
  private VBox vbAttendees;

  private ImageView imgUserReaction;

  private MasterController master;

  @FXML
  void attendEvent(ActionEvent event) throws IOException {
    setAttending(!attending);
  }

  @FXML
  void spinReaction(MouseEvent event) {
    // Gets the image that is hovered
    ImageView reaction = ((ImageView) event.getSource());
    spinEmoji(reaction);
  }

  @FXML
  void unSpin(MouseEvent event) {
    // Get image that was hovered
    ImageView reaction = ((ImageView) event.getSource());
    stopEmoji(reaction);
  }

  @FXML
  void showDetails(ActionEvent event) {
    boolean showDetails = !(pnEventDetails.isVisible());
    spAllEvent.setMaxHeight((showDetails ? normalHeight : normalHeight - detailsHeight));
    btnShowDetails.setText((showDetails ? "Hide Details" : "Show Details"));
    pnEventDetails.setVisible(showDetails);

    if (imgUserReaction != null) {
      stopEmoji(imgUserReaction);
      spinEmoji(imgUserReaction);
    }
  }

  @FXML
  void buttonOnMouseEnter(MouseEvent event) {
    if (event.getSource() instanceof Button) {
      ((Button) event.getSource()).setOpacity(0.8);
    } else {
      ((ToggleButton) event.getSource()).setOpacity(0.8);
    }
  }

  @FXML
  void buttonOnMouseExited(MouseEvent event) {
    if (event.getSource() instanceof Button) {
      ((Button) event.getSource()).setOpacity(1);
    } else {
      ((ToggleButton) event.getSource()).setOpacity(1);
    }
  }

  @FXML
  void reactEvent(MouseEvent event) {
    ImageView reactionImage = ((ImageView) event.getSource());
    int reaction;
    String userEmail = master.getCurrentUser().getEmail();
    String name = master.getCurrentUser().getFirstName() + " " + master.getCurrentUser().getLastName();
    if (reactionImage.equals(imgReactionOne)) {
      reaction = 1;
    } else if (reactionImage.equals(imgReactionTwo)) {
      reaction = 2;
    } else if (reactionImage.equals(imgReactionThree)) {
      reaction = 3;
    } else {
      reaction = 4;
    }
    this.event.addReaction(userEmail, reaction, name);
    addReactions();
    master.saveEvents();
  }

  @FXML
  void showInfo(MouseEvent event) {
    if (event.getSource() == lblReactions || event.getSource() == stpReactions) {
      stpReactions.setVisible(true);
    } else {
      stpAttendees.setVisible(true);
    }
  }

  @FXML
  void closeInfo(MouseEvent event) {
    if (event.getSource() == lblReactions || event.getSource() == stpReactions) {
      stpReactions.setVisible(false);
    } else {
      stpAttendees.setVisible(false);
    }
  }

  public void load(Event event) {
    setEvent(event);
    setEventInfo();
    addReactions();
    addAttendees();
  }

  private void setEventInfo() {
    String title = event.getTitle();
    this.lblTitle.setText(title);

    String date = String.valueOf(event.getDate()).substring(0, 10);
    this.lblDate.setText(date);

    String location = event.getLocation();
    this.lblLocation.setText(location);

    String time = event.getTime();
    this.lblTime.setText(time);

    String attendees = String.valueOf(event.getAttending().size());
    this.lblAttendees.setText(attendees);

    String details = event.getDescription();
    this.txtDescription.setText(details);

    // Get full height of event and only detail's height.
    normalHeight = spAllEvent.getPrefHeight();
    detailsHeight = pnEventDetails.getPrefHeight();

    // Remove event detail's height from the event.
    double smallHeight = normalHeight - detailsHeight;
    spAllEvent.setMaxHeight(smallHeight);

    // Controlling functionality of event buttons
    boolean eventPassed = event.getDate().isBefore(LocalDate.now());
    btnAttend.setDisable(eventPassed);
    imgReactionOne.setDisable(eventPassed);
    imgReactionTwo.setDisable(eventPassed);
    imgReactionThree.setDisable(eventPassed);
    imgReactionFour.setDisable(eventPassed);

    boolean isStudent = master.getCurrentUser().getType() < 3;
    btnAttend.setVisible(isStudent);

    String userEmail = master.getCurrentUser().getEmail();
    attending = event.getAttending().contains(userEmail);

    if (attending) {
      btnAttend.setStyle("-fx-background-color: rgb(246, 168, 166);");
      btnAttend.setText("Cancel");
    } else {
      btnAttend.setStyle("-fx-background-color: rgb(192,236,204);");
      btnAttend.setText("Attend");
    }

    pnEventDetails.setVisible(false);
  }
  
  public void setMaster(MasterController master) {
    this.master = master;
  }

  private void setEvent(Event event) {
    this.event = event;
  }

  private void setAttending(boolean status) {
    this.attending = status;
    String userEmail = master.getCurrentUser().getEmail();
    if (attending) {
      event.addAttendee(userEmail);
      btnAttend.setStyle("-fx-background-color: rgb(246, 168, 166);");
      btnAttend.setText("Cancel");
    } else {
      event.delAttendee(userEmail);
      btnAttend.setStyle("-fx-background-color: rgb(192,236,204);");
      btnAttend.setText("Attend");
    }
    master.saveEvents();
    setEventInfo();
  }

  void addReactions() {
    int reactions = event.getReactions().size();
    lblReactions.setText("" + reactions);
    loadReactionList();
  }

  void loadReactionList() {
    // Retrieve all reactions in a sorted ArrayList.
    ArrayList<String[]> reactions = sortReactions(event.getReactions());
    int size = reactions.size();

    // Create a new array of nodes with either 2 slots or the size of arraylist.
    Node[] reactionList = new Node[(size == 0 ? 1 : size)];

    // Loop through all the reactions if any
    for (int i = 0; i < size; i++) {
      String name = reactions.get(i)[2];
      String img = reactions.get(i)[1];
      reactionList[i] = infoPane(name, img, false);
    }

    // If there are no reactions we let the user know
    // If there's only one reaction we'll add an empty one to make colors look good.
    if (size == 0) {
      final String noReacts = "No reactions yet.";
      final boolean empty = true;

      reactionList[0] = infoPane(noReacts, null, empty);
    }
    vbReactions.setSpacing(2);
    vbReactions.getChildren().setAll(reactionList);
    vbReactions.setStyle("-fx-background-color:#FFF");
    int height = size > 2 ? (size > 5 ? 5 * 20 : size * 20) : 40;
    double layoutY = 100 - height;
    stpReactions.setLayoutY(layoutY);
  }

  /*
    This method will first loop through 1-4 (The reactions available)
    It will in each iteration loop through all the reactions
    It will then check if the reaction of the event is the same as the reaction we're
    -checking(1 through 4).
    If it is a match it will add that reaction to another arraylist and go to the next one.
    Thus, we are sorting them based on reaction 1, 2, 3 and 4.
   */

  private ArrayList<String[]> sortReactions(ArrayList<String[]> reactions) {
    ArrayList<String[]> sortedReactions = new ArrayList<>();
    String userEmail = master.getCurrentUser().getEmail();
    for (int i = 1; i < 5; i++) {
      for (String[] reaction : reactions) {
        switch (i) {
          case 1:
            if (reaction[1].equals("1")) {
              sortedReactions.add(reaction);
              if(reaction[0].equals(userEmail)) {
                imgUserReaction = imgReactionOne;
              }
            }
            break;
          case 2:
            if (reaction[1].equals("2")) {
              sortedReactions.add(reaction);
              if(reaction[0].equals(userEmail)) {
                imgUserReaction = imgReactionTwo;
              }
            }
            break;
          case 3:
            if (reaction[1].equals("3")) {
              sortedReactions.add(reaction);
              if(reaction[0].equals(userEmail)) {
                imgUserReaction = imgReactionThree;
              }
            }
            break;
          default:
            if (reaction[1].equals("4")) {
              sortedReactions.add(reaction);
              if(reaction[0].equals(userEmail)) {
                imgUserReaction = imgReactionFour;
              }
            }
        }
      }
    }
    return sortedReactions;
  }

  private void addAttendees() {
    String attendees = "" + event.getAttending().size();
    lblAttendees.setText(attendees);
    loadAttendeesList();
  }

  private void loadAttendeesList() {
    // Retrieve all attendees for event
    ArrayList<String> attendees = event.getAttending();
    int size = attendees.size();
    Node[] attendeeList = new Node[(size > 0 ? size : 1)];
    for (int i = 0; i < size; i++) {
      String first = master.findUser(attendees.get(i)).getFirstName();
      String last = master.findUser(attendees.get(i)).getLastName();
      String name = first + " " + last;

      attendeeList[i] = infoPane(name, "", false);
    }
    if (size == 0) {
      attendeeList[0] = infoPane("No attendees yet.",  "",  true);
    }

    vbAttendees.setSpacing(2);
    vbAttendees.getChildren().setAll(attendeeList);
    vbAttendees.setStyle("-fx-background-color:#FFF");
    int height = size > 2 ? (size > 15 ? 15 * 14 : size * 14) : 0;
    double layoutY = 210 - height;
    stpAttendees.setLayoutY(layoutY);
  }

  private Pane infoPane(String text, String react, boolean empty) {
    Pane pnReaction = new Pane();
    pnReaction.setStyle("-fx-background-color:#FFF");
    Label lblReactionText = new Label(text);

    if (empty) {
      lblReactionText.setStyle("-fx-font-weight: bold;");
      lblReactionText.setPrefWidth(150);
      lblReactionText.setAlignment(Pos.TOP_CENTER);
    } else {
      String img = switch (react) {
        case "1" -> String.valueOf(MasterController.class.getResource("images/reaction_one.png"));
        case "2" -> String.valueOf(MasterController.class.getResource("images/reaction_two.png"));
        case "3" -> String.valueOf(MasterController.class.getResource("images/reaction_three.png"));
        default -> String.valueOf(MasterController.class.getResource("images/reaction_four.png"));
      };
      ImageView ivEmoji = new ImageView(img);
      ivEmoji.maxWidth(16);
      ivEmoji.maxHeight(16);
      ivEmoji.setFitHeight(16);
      ivEmoji.setFitWidth(16);
      ivEmoji.setLayoutX(5);
      lblReactionText.setLayoutX(26);
      pnReaction.getChildren().add(ivEmoji);
    }
    pnReaction.getChildren().add(lblReactionText);
    return pnReaction;
  }

  private void spinEmoji(ImageView emoji) {

    // Our animation for rotating picture left
    RotateTransition rotateLeft = new RotateTransition(Duration.millis(100), emoji);
    rotateLeft.setByAngle(15); // Rotate 15 degrees to left
    rotateLeft.setCycleCount(1); // Rotate once
    rotateLeft.setInterpolator(Interpolator.LINEAR);

    // Our animation for rotating picture right
    RotateTransition rotateRight = new RotateTransition(Duration.millis(100), emoji);
    rotateRight.setByAngle(-30); // Rotates 30 degrees to the right(15 degrees from 0)
    rotateRight.setCycleCount(1); // Rotate once
    rotateRight.setInterpolator(Interpolator.LINEAR);

    // Slight paus in animation for smoothness
    RotateTransition rotatePaus = new RotateTransition(Duration.millis(1000), emoji);
    rotatePaus.setByAngle(0); // Does not rotate at all
    rotateRight.setCycleCount(1); // Pauses only once

    // Get image's current rotation
    double yx = emoji.getLocalToSceneTransform().getMyx();
    double yy = emoji.getLocalToSceneTransform().getMyy();

    // Then calculate the angle that needs to be reverse for rotation 0
    double angle = -(Math.atan2(yx, yy) * 100) / 2;

    // Create a new rotation animation, play it once and make it return to 0 degrees angle.
    RotateTransition evenOut = new RotateTransition(Duration.millis(100), emoji);
    evenOut.setByAngle(15);
    evenOut.setCycleCount(1);
    evenOut.setInterpolator(Interpolator.LINEAR);

    SequentialTransition emojiSequence = new SequentialTransition(rotateLeft, rotateRight, evenOut); // First rotate left, then right, then paus
    emojiSequence.setCycleCount(3); // rotate 3 times then pause
    emojiAnimation = new SequentialTransition(emojiSequence, rotatePaus);
    emojiAnimation.setCycleCount(Timeline.INDEFINITE);
    emojiAnimation.play(); // Start sequence.
  }

  private void stopEmoji(ImageView image) {
    if (image != null) {
      // Stop the sequence of rotations
      emojiAnimation.stop();
    }
    // Get image's current rotation
    double yx = image.getLocalToSceneTransform().getMyx();
    double yy = image.getLocalToSceneTransform().getMyy();

    // Then calculate the angle that needs to be reverse for rotation
    double angle = -(Math.atan2(yx, yy) * 100) / 2;

    // Create a new rotation animation, play it once and make it return to 0 degrees angle.
    RotateTransition evenOut = new RotateTransition(Duration.millis(10), image);
    evenOut.setByAngle(angle);
    evenOut.setCycleCount(1);
    evenOut.setInterpolator(Interpolator.LINEAR);
    evenOut.play();
  }

}