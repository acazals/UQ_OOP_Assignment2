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
    private String filename;


    public ExamBlockModel() {

        this.myObservers = new ArrayList<>();
        this.myExams = new ExamList(getRegistry());
        //this.myRegistry = null; // un initialized
        this.myRooms = new RoomList(this.getRegistry());
        this.mySessions = new SessionList(this.getRegistry());
        this.myStudents = new StudentList(this.getRegistry());
        this.mySubjects = new SubjectList(this.getRegistry());
        this.myUnits = new UnitList(this.getRegistry());
        this.myVenues = new VenueList(this.getRegistry());
        this.Title = "Exam Block Model"; // default?
        this.Version = 1.0;

        }
    public void addObserver(ModelObserver observer) {
        this.myObservers.add(observer);
    }

    private String getFilename() {
        return this.filename;
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

    public void setFilename( String filename) {
        this.filename = filename;
    }

    public void setTitle( String title) {
        this.Title = title;
    }

    public void setVersion(double version) {
        if (this.Version <= version) {
            this.Version = version;
        }
    }




}
