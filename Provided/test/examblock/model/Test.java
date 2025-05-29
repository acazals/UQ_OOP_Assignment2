//package examblock.model;
//
//import examblock.view.components.Verbose;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.IOException;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.time.format.DateTimeFormatter;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class Test {
//    ///  stream in and out methods for Sessions :
//    @Override
//    public void streamOut(BufferedWriter bw, int nthItem) throws IOException {
//        // Header line
//        String venueNames = this.getVenue().getRooms().
//                .map(Venue::getId)
//                .collect(Collectors.joining("+"));
//
//        LocalDate date = getDate(); // assume getDate() returns LocalDate
//        LocalTime time = this.getTime(); // assume getStartTime() returns LocalTime
//
//        bw.write(nthItem + ". Venue: " + venueNames +
//                ", Session Number: " + sessionNumber +
//                ", Day: " + date +
//                ", Start: " + time +
//                ", Student Count: " + this.get +
//                ", Exams: " + exams.size());
//        bw.newLine();
//
//        for (Exam exam : exams) {
//            bw.write(exam.getTitle());
//            bw.newLine();
//
//            List<Desk> desks = exam.getDesks();
//            bw.write("[Desks: " + desks.size() + "]");
//            bw.newLine();
//
//            for (Desk desk : desks) {
//                desk.streamOut(bw);
//            }
//        }
//    }
//
//    private String generateExamId(LocalDate mydate, String title) {
//        StringBuilder sb = new StringBuilder();
//
//        // part 1 : subject title
//
//        if (title != null && title!= "") {
//
//            sb.append(title
//                    .trim()
//                    .replaceAll("\\s+", "_") // Replace one or more spaces with a single underscore
//                    .toUpperCase()); // Convert to uppercase for consistency
//        } else {
//            sb.append("UNKNOWN_SUBJECT"); // Fallback if subject or title is null
//        }
//
//        // 2. Append  Date
//        // Using date  (YYYYMMDD)
//        String date = mydate.format(DateTimeFormatter.BASIC_ISO_DATE); // YYYYMMDD
//        sb.append("_").append(date); // Append with an underscore separator
//
//        return sb.toString();
//    }
//
//
//
//    private int MetaDataParsing(String[] fields, Registry registry) {
//
//        String venueId = "";
//        int examCount = 0;
//
//        for (String field : fields) {
//            if (field.startsWith("Venue:")) {
//                venueId = field.split(":")[1].trim();
//                this.venue = registry.get(venueId, Venue.class);
//            } else if (field.startsWith("Session Number:")) {
//                this.sessionNumber = CSSE7023.toInt(field.split(":")[1].trim(), " can t convert session number to an int");
//            } else if (field.startsWith("Day:")) {
//                this.day = CSSE7023.toLocalDate(field.split(":")[1].trim(), " can t convert to a date");
//            } else if (field.startsWith("Start:")) {
//                this.start = CSSE7023.toLocalTime(field.split(":")[1].trim(), " Can t convert to a time");
//            } else if (field.startsWith("Student Count:")) {
//                this.studentCount = CSSE7023.toInt(field.split(":")[1].trim(), "Can t convert studentcount to an int");
//            } else if (field.startsWith("Exams:")) {
//                examCount = Integer.parseInt(field.split(":")[1].trim());
//                // number of exams
//            }
//        }
//        return examCount;
//
//    }
//
//    private void DeskParsing(String[] myDesks, Registry registry) throws IOException {
//        for ( String myLine : myDesks) {
//            if (myLine.startsWith("Year 12")) {
//                this.getExam(myLine, this.getDate(), registry );
//            }  if (myLine.startsWith("[Desk:")) {
//                continue;
//            }  if (myLine.startsWith("Desk")){
//                //Desk: 1, LUI: 9999493906, Name: Black, Mitchell C.
//            }
//            // parse desk
//        } else {
//            throw new IOException();
//        }
//    }
//}
//
//private void getExam(String examLine, LocalDate date, Registry registry) {
//    String examTitle = "";
//    if (examLine.startsWith("Year 12 Internal Assessment ")) {
//        examTitle = examLine.substring("Year 12 Internal Assessment ".length()).trim();
//        String examId = this.generateExamId(date, examTitle);
//        this.exams.add(registry.get(examId, Exam.class));
//    } else if (examLine.startsWith("Year 12 External Assessment ")) {
//        examTitle = examLine.substring("Year 12 External Assessment ".length()).trim();
//        String examId = this.generateExamId(date, examTitle);
//        this.exams.add(registry.get(examId, Exam.class));
//    } else {
//        throw new RuntimeException("Invalid exam line: " + examLine);
//    }
//}
//
///***
// * 1. Venue: V1+V2+V3, Session Number: 1, Day: 2025-03-10, Start: 12:30, Student Count: 53, Exams: 2
// * Year 12 Internal Assessment Literature
// *
// * @param br         reader, already opened
// * @param registry the global object registry
// * @param nthItem    a number representing this item's position in the stream. Used for sanity
// *                   checks
// * @throws IOException
// */
//
//@Override
//public void streamIn(BufferedReader br, Registry registry, int nthItem) throws IOException {
//    StringBuilder fullText = new StringBuilder();
//    String line;
//    while ((line = br.readLine()) != null) { // we use br.readline to stop at a blank line
//        fullText.append(line).append("\n");
//    }
//
//    // Split the entire session block into individual sessions.
//    // Each session starts with a line like "1. Venue: ..." or "2. Venue: ..."
//    // The regex uses a positive lookahead to split the text *before* each such line,
//    // so that the session header line is preserved in each resulting chunk.
//    // Example: "1. Venue: ..." stays at the beginning of the first chunk,
//    //          "2. Venue: ..." at the beginning of the second chunk, etc.
//    String[] sessionChunks = fullText.toString().split("(?=\\d+\\.\\s+Venue:)");
//
//    for (int i = 0; i < sessionChunks.length; i++) { // for each venue
//        // first : split before the desk list
//        String[] examChunks = sessionChunks[i].split("(?=Year 12 (Internal|External) Assessment)");
//
//        String metadata = examChunks[0];
//
//
//        String metadataCleaned = metadata.replaceFirst("^\\d+\\.\\s*", "").trim();
//        //  taking off the 1. ; only keep : venue list : exam, students ect
//        String[] parts = metadataCleaned.split(", ");
//        // split from the commas
//        // no sanity check here ?? because we only have the number of the venues...
//        int nbExam = this.MetaDataParsing(parts, registry);
//
//        // now on to the part with all desks :
//
//        for (int j = 1; i < examChunks.length; i++) {
//            /**  here we work with somehing like this :
//             * Year 12 Internal Assessment Literature
//             * [Desks: 36]
//             * Desk: 1, LUI: 9999831170, Name: Ahmad, Tariq N.
//             * Desk: 2, LUI: 9999353258, Name: Black, Eliza G.
//             * Desk: 3, LUI: 9999257005, Name: Carpenter, Noah E.
//             * */
//
//            String[] deskLines = examChunks[j].split("\\R");
//            // split per line
//
//
//            if (Verbose.isVerbose()) {
//                System.out.println("Loaded Session #" + (nthItem + i));
//            }
//        }
//    }
//
//
//}
//}
//
//
