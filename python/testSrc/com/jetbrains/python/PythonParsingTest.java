package com.jetbrains.python;

import com.intellij.psi.PsiFile;
import com.intellij.testFramework.ParsingTestCase;
import com.intellij.testFramework.TestDataPath;
import com.jetbrains.cython.CythonTokenSetContributor;
import com.jetbrains.python.fixtures.PyTestCase;
import com.jetbrains.python.psi.LanguageLevel;

/**
 * @author yole
 */
@TestDataPath("$CONTENT_ROOT/../testData/psi/")
public class PythonParsingTest extends ParsingTestCase {
  private LanguageLevel myLanguageLevel = LanguageLevel.getDefault();

  public PythonParsingTest() {
    super("psi", "py", new PythonParserDefinition());
    PyTestCase.initPlatformPrefix();
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    registerExtensionPoint(PythonDialectsTokenSetContributor.EP_NAME, PythonDialectsTokenSetContributor.class);
    registerExtension(PythonDialectsTokenSetContributor.EP_NAME, new PythonTokenSetContributor());
    registerExtension(PythonDialectsTokenSetContributor.EP_NAME, new CythonTokenSetContributor());
  }

  @Override
  protected String getTestDataPath() {
    return PythonTestUtil.getTestDataPath();
  }

  public void testHelloWorld() {
    doTest();
  }

  public void testIfStatement() {
    doTest();
  }

  public void testConditionalExpression() {
    doTest();
  }

  public void testSubscribedAssignmentLHS() {
    doTest();
  }

  public void testConditionalParenLambda() {
    doTest();
  }

  public void testLambdaComprehension() {
    doTest();
  }

  public void testLambdaConditional() {
    doTest();
  }

  public void testTryExceptFinally() {
    doTest();
  }

  public void testTryFinally() {
    doTest();
  }

  public void testYieldStatement() {
    doTest();
  }

  public void testYieldInAssignment() {
    doTest();
  }

  public void testYieldInAugAssignment() {
    doTest();
  }

  public void testYieldInParentheses() {
    doTest();
  }

  public void _testYieldAsArgument() {
    // this is a strange case: PEP 342 says this syntax is valid, but
    // Python 2.5 doesn't accept it. let's stick with Python behavior for now
    doTest();
  }

  public void testWithStatement() {
    doTest();
  }

  public void testWithStatement2() {
    doTest();
  }

  public void testImportStmt() {
    doTest();
  }

  public void testDecoratedFunction() {
    doTest();
  }

  public void testTryExceptAs() {   // PY-293
    doTest();
  }

  public void testWithStatement26() {
    doTest(LanguageLevel.PYTHON26);
  }

  public void testPrintAsFunction26() {
    doTest(LanguageLevel.PYTHON26);
  }

  public void testClassDecorators() {
    doTest(LanguageLevel.PYTHON26);
  }

  public void testEmptySuperclassList() {  // PY-321
    doTest();
  }

  public void testListComprehensionNestedIf() {  // PY-322
    doTest();
  }

  public void testKeywordOnlyArgument() {   // PEP 3102
    doTest(LanguageLevel.PYTHON30);
  }

  public void testPy3KKeywords() {
    doTest(LanguageLevel.PYTHON30);
  }

  public void testExecPy2() {
    doTest();
  }

  public void testExecPy3() {
    doTest(LanguageLevel.PYTHON30);
  }

  public void testSuperclassKeywordArguments() {
    doTest(LanguageLevel.PYTHON30);
  }

  public void testDictLiteral() {
    doTest();
  }

  public void testSetLiteral() {
    doTest(LanguageLevel.PYTHON30);
  }

  public void testSetComprehension() {
    doTest(LanguageLevel.PYTHON30);
  }

  public void testDictComprehension() {
    doTest(LanguageLevel.PYTHON30);
  }

  public void testRaiseFrom() {
    doTest(LanguageLevel.PYTHON30);
  }

  public void testEllipsis() {
    doTest();
  }

  public void testTupleArguments() {
    doTest();
  }

  public void testDefaultTupleArguments() {
    doTest();
  }

  public void testExtendedSlices() {
    doTest();
  }

  public void testAnnotations() {
    doTest(LanguageLevel.PYTHON30);
  }

  public void testNonlocal() {
    doTest(LanguageLevel.PYTHON30);
  }

  public void testFloorDiv() {
    doTest();
  }

  public void testWithStatement31() {
    doTest(LanguageLevel.PYTHON31);
  }

  public void testLongString() {
    doTest();
  }

  public void testTrailingSemicolon() {  // PY-363
    doTest();    
  }

  public void testStarExpression() {   // PEP-3132
    doTest(LanguageLevel.PYTHON30);
  }

  public void testDictMissingComma() {  // PY-1025
    doTest();
  }

  public void testInconsistentDedent() { // PY-1131
    doTest();
  }

  public void testReturnAtEOF() {  // PY-1739
    doTest();
  }

  public void testMissingListSeparators() {  // PY-1933
    doTest();
  }

  public void testTrailingCommaInList() {
    doTest();
  }

  public void testCommentBeforeMethod() { // PY-2209 & friends
    doTest();
  }

  public void testCommentAtEndOfMethod() { // PY-2137
    doTest();
  }

  public void testCommentAtBeginningOfStatementList() {  // PY-2108
    doTest();
  }

  public void testCommentBetweenClasses() {  // PY-1598
    doTest();
  }

  public void testIncompleteDict() {
    doTest();
  }

  public void testSliceList() {  // PY-1928
    doTest();
  }

  public void testDictMissingValue() {  // PY-2791
    doTest();
  }

  public void testColonBeforeEof() {  // PY-2790
    doTest();
  }

  public void testGeneratorInArgumentList() {  // PY-3172
    doTest();
  }

  public void testNestedGenerators() {  // PY-3030
    doTest();
  }

  public void testMissingDefaultValue() {  // PY-3253
    doTest();
  }

  public void testErrorInParameterList() {  // PY-3635
    doTest();
  }

  public void testKeywordAsDefaultParameterValue() {  // PY-3713
    doTest();
  }

  public void testTrailingCommaInArgList() {  // PY-4016
    doTest();
  }

  public void testMissingParenInCall() {  // PY-4053
    doTest();
  }

  public void testTupleAsDictKey() {  // PY-4144
    doTest();
  }

  public void testIncompleteStatementList() {  // PY-3792
    doTest();
  }

  public void testIncompleteFor() {  // PY-3792
    doTest();
  }
  
  public void testCallInAssignment() {  // PY-5062
    doTest();
  }

  public void testCommaAfterStarArg() {  // PY-4039
    doTest();
  }

  public void testRangeAsLHS() {  // PY-6468
    doTest();
  }

  // PY-6702
  public void testYieldFrom() {
    doTest(LanguageLevel.PYTHON33);
  }

  public void doTest() {
    doTest(LanguageLevel.PYTHON25);
  }


  public void doTest(LanguageLevel languageLevel) {
    LanguageLevel prev = myLanguageLevel;
    myLanguageLevel = languageLevel;
    try {
      doTest(true);
    }
    finally {
      myLanguageLevel = prev;
    }
  }

  @Override
  protected PsiFile createFile(String name, String text) {
    final PsiFile file = super.createFile(name, text);
    file.getVirtualFile().putUserData(LanguageLevel.KEY, myLanguageLevel);
    return file;
  }
}
