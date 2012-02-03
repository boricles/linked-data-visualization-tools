package es.upm.fi.dia.oeg.map4rdf.compiler;

import org.apache.tools.ant.taskdefs.Javac;
import org.eclipse.jdt.core.JDTCompilerAdapter;


public class JDTCompiler16 extends JDTCompilerAdapter {
  
  @Override
  public void setJavac(Javac attributes) {
    if (attributes.getTarget() == null) {
      attributes.setTarget("1.6");
    }
    if (attributes.getSource() == null) {
      attributes.setSource("1.6");
    }
    super.setJavac(attributes);
  }
  
}

