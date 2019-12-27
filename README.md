# SVGToXML DEVS Model Converter

The DEVS formalism is a widely implemented modeling and simulation method used for both artificial and natural systems. 
Scalable Vector graphics.Graphics (SVG) is a vector image format based on the Extensible Markup language (XML) for creating 
two-dimensional graphics that can be animated and are interactive. With the increasing need of modeling and simulation, 
most domain experts may have ideas of model they would want to create and run simulated tests with, but have little or 
no idea in computer programing. The aim of this project is to simplify the creation and simulation of DEVS model by allowing 
the user draw the model that needs to be simulated in SVG and executing it without writing any computer program. This is 
done by converting the drawn SVG to XML, and then to C++ code ready for execution with Cadmium which is a C++ based 
library used for defining and executing DEVS models. This paper specifies the standard for defining DEVS coupled model 
in SVG format and converting them to XML using a parser we implemented. The generated XML files can then be further used
 to generate C++ Cadmium code for execution
 
 ## Organization
 > - University of Ottawa

## Authors
> - Chidiebere Onyedinma <conye066@uottawa.ca> University of Ottawa


## Building and Usage
The parser [Java](https://www.java.com/en/download/) source files can be built and packaged using [Maven](https://maven.apache.org/) which is a software project management and comprehension tool.
> - To build the source files simply install [Maven](https://maven.apache.org/) and add it to “path” environment variable.
> - Install [Java](https://www.java.com/en/download/) JDK version 8 and above
> - Go to the root directory of the project (SVGToXML) and run the following command:

````shell script
mvn clean compile package
````

This will generate a target folder with the **svg-xml.jar** file inside.<br>
> - If not existing, create a directory called *svgfiles* in the same location as the jar file and put your svg files inside. Some example SVG Files have been created to test the application, and can be found in the [svgfiles](https://github.com/blacktorch/SVGToXML/tree/master/svgfiles) folder
> - To parse a svg file, simple run the following command:

````shell script
java -jar svg-xml.jar yourSVGfile.svg
````
> - The generated XML files will be in a directory called **XMLFiles**.

## Documentation
A detailed paper named **Specifications and Rules.pdf** with the specifications and rules on how to created and SVG DEVS model can be found in the [docs](https://github.com/blacktorch/SVGToXML/tree/master/docs) directory.