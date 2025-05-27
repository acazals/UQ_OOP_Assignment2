package examblock.model;

import java.util.ArrayList;

public class ExamBlockModel {

    private ArrayList<ModelObserver> myObservers;
    private ExamList myExams;
    private Registry myRegistry;
    private RoomList myRooms;
    private SessionList mySessions;
    private StudentList myStudents;
    private SubjectList mySubjects;
    private String Title;
    private UnitList myUnits;
    private VenueList myVenues;
    private double Version;


    public ExamBlockModel() {

        this.myObservers = new ArrayList<>();
        this.myExams = new ExamList();
        //this.myRegistry = null; // un initialized
        this.myRooms = new RoomList(this.getRegistry());
        this.mySessions = new SessionList();
        this.myStudents = new StudentList();
        this.mySubjects = new SubjectList();
        this.myUnits = new UnitList();
        this.myVenues = new VenueList();
        this.Title = "Exam Block Model"; // You can change this default if needed
        this.Version = 1.0;
        }
    public void addObserver(ModelObserver observer) {
        this.myObservers.add(observer);
    }

    public ExamList getExams() {
        return this.myExams;
    }

    public Registry getRegistry() {
        return this.myRegistry;
    }

    public RoomList getRooms() {
        return this.myRooms;
    }

    public SessionList getSessions() {
        return this.mySessions;
    }

    public StudentList getStudents() {
        return this.myStudents;
    }

    public SubjectList getSubjects() {
        return this.mySubjects;
    }

    public String getTitle() {
        return this.Title;
    }

    public UnitList getUnits() {
        return this.myUnits;
    }

    public VenueList getVenues() {
        return this.myVenues;
    }

    public double getVersion() {
        return this.Version;
    }

    public void notifyObservers(String property) {
        for (ModelObserver observer : myObservers) {
            observer.modelChanged(property);
        }
    }




}
