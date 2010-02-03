package com.jetbrains.python;

import com.jetbrains.python.refactoring.PyExtractMethodTest;
import com.jetbrains.python.refactoring.PyRenameTest;
import com.jetbrains.python.refactoring.classes.PyExtractSuperclassTest;
import com.jetbrains.python.refactoring.classes.PyPullUpTest;
import com.jetbrains.python.refactoring.classes.PyPushDownTest;
import junit.framework.TestSuite;

/**
 * A suite to explicitly include all actual tests, in fail-fast order.
 * User: dcheryasov
 * Date: Nov 19, 2009 1:23:22 AM
 */
public class PythonAllTestsSuite {

  public static final Class[] tests = {
    PythonLexerTest.class,
    PythonParsingTest.class,
    PyStringLiteralTest.class,
    PyIndentTest.class,
    PyStatementPartsTest.class,
    PythonHighlightingTest.class,
    PyStubsTest.class,
    PyResolveTest.class,
    PyMultiFileResolveTest.class,
    PyResolveCalleeTest.class,
    PyAssignmentMappingTest.class,
    PythonCompletionTest.class,
    PyInheritorsSearchTest.class,
    PyParameterInfoTest.class,
    PyDecoratorTest.class,
    PyQuickDocTest.class,
    PythonInspectionsTest.class,
    PythonDemorganLawIntentionTest.class,
    PyQuickFixTest.class,
    PySelectWordTest.class,
    PySurroundWithTest.class,
    PyEditingTest.class,
    PyFormatterTest.class,
    PyRenameTest.class,
    PyExtractMethodTest.class,
    PyPullUpTest.class,
    PyPushDownTest.class,
    PyExtractSuperclassTest.class,
  };

  public static TestSuite suite() {
    return new TestSuite(tests);
  }

  
}
