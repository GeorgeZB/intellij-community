/*
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.groovy.codeInspection.changeToOperator.transformations;

import org.jetbrains.plugins.groovy.codeInspection.changeToOperator.data.MethodCallData;
import org.jetbrains.plugins.groovy.codeInspection.changeToOperator.data.OptionsData;

import static org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes.mLPAREN;
import static org.jetbrains.plugins.groovy.lang.lexer.GroovyTokenTypes.mRPAREN;

class CallTransformation extends UnaryTransformation {
  public static final String EMPTY_PARENS = mLPAREN.toString() + mRPAREN;

  public CallTransformation() {
    super(null);
  }

  @Override
  public String getReplacement(MethodCallData call, OptionsData options) {
    return super.getReplacement(call, options) + EMPTY_PARENS;
  }
}
