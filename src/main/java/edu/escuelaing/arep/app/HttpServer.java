package edu.escuelaing.arep.app;
import java.net.*;
import java.io.*;
import java.lang.reflect.*;

public class HttpServer {
  public static void main(String[] args) throws IOException {
   ServerSocket serverSocket = null;
   try { 
      serverSocket = new ServerSocket(36000);
   } catch (IOException e) {
      System.err.println("Could not listen on port: 35000.");
      System.exit(1);
   }

   while(true){
      Socket clientSocket = null;
      try {
         System.out.println("Listo para recibir ...");
         clientSocket = serverSocket.accept();
      } catch (IOException e) {
         System.err.println("Accept failed.");
         System.exit(1);
      }
      PrintWriter out = new PrintWriter(
                           clientSocket.getOutputStream(), true);
      BufferedReader in = new BufferedReader(
                           new InputStreamReader(clientSocket.getInputStream()));
      String inputLine;
      String outputLine = "";
      boolean firstLine = true;
      String uriString = "";
      while ((inputLine = in.readLine()) != null) {
         System.out.println("Recibí: " + inputLine);
         if (firstLine){
            firstLine = false;
            uriString = inputLine.split(" ")[1];
            System.out.println("URI: " + uriString);
            if(uriString.contains("consulta")){
               outputLine = getCommandResponse(uriString.split("consulta\\?comando=")[1]);
            }else{
               outputLine = getIndexResponse();
            }
        }
         if (!in.ready()) {break; }
      }
      out.println(outputLine);
      out.close(); 
      in.close();
      clientSocket.close();      
      }
   }

   public static String getIndexResponse(){
   String response = "HTTP/1.1 200 OK\r\n"
   + "Content-Type: text/html\r\n"
   + "\r\n"
   + "<!DOCTYPE html>\r\n"
   + "<html>\r\n"
   + "<head>\r\n"
   + "<title>AREP Parcial 1</title>\r\n"
   + "<meta charset=\"UTF-8\">\r\n"
   + "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\r\n"
   + "</head>\r\n"
   + "<body>\r\n"
   + "<h1>Comando Class:</h1>\r\n" // Comando Class
   + "<form action=\"/consulta?comando=Class\">\r\n"
   + "<label for=\"className\">Comando:</label><br>\r\n"
   + "<input type=\"text\" id=\"className\" name=\"name\" value=\"\"><br><br>\r\n"
   + "<input type=\"button\" value=\"Submit\" onclick=\"loadClassMsg()\">"
   + "</form>\r\n"
   + "<div id=\"classmsg\"></div>\r\n"
   + "<script>\r\n"
   + "function loadClassMsg(){\r\n"
   + "let nameVar = document.getElementById(\"className\").value;\r\n"
   + "const xhttp = new XMLHttpRequest();\r\n"
   + "xhttp.onload = function() {\r\n"
   + "document.getElementById(\"classmsg\").innerHTML =\r\n"
   + "this.responseText;\r\n"
   + "}\r\n"
   + "xhttp.open(\"GET\", \"/consulta?comando=\"+nameVar);"
   + "xhttp.send();"
   + "}\r\n"
   + "</script>\r\n"
   + "</body>\r\n"
   + "</html>\r\n";
   return response;
   }

   public static String getCommandResponse(String comand){
      String answer = "Respuesta: ";
      System.out.println("Comando solicitado: " + comand);
      try{
         if(comand.contains("Class")){
            String value = comand.split("\\(")[1];
            value = value.split("\\)")[0];
            System.out.println("Valor: " + value);
            Class c = Class.forName(value);
            Method[] methods = c.getDeclaredMethods();
            Field[] fields = c.getDeclaredFields();
            for(Method method : methods){
               answer += method.toString() + "\r\n";
            }
            for(Field field : fields){
               answer += field.toString() + "\r\n";
            }
         }else if(comand.contains("invoke")){
         }
      }catch(Exception e){
         answer = "Error";
      }
      String response = "HTTP/1.1 200 OK\r\n"
      + "Content-Type: text/html\r\n"
       + "\r\n"
       + "<!DOCTYPE html>\n"
       + "<html>\n"
       + "<head>\n"
       + "<meta charset=\"UTF-8\">\n"
       + "</head>\n"
       + "<body>\n"
       + "<h3>" + answer + "</h3>\n"
       + "</body>\n"
       + "</html>\n";
      return response;
   }
}
