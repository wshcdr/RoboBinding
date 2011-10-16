/**
 * BooleanToStringConverterTest.java
 * Oct 16, 2011 Copyright Cheng Wei and Robert Taylor
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions
 * and limitations under the License.
 */
package robobinding.value;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

/**
 * @since 1.0
 * @version $Revision:  $
 * @author Cheng Wei
 *
 */
public class BooleanToStringConverterTest
{
	private BooleanToStringConverterTester tester;
	
	private static final String TRUE_TEXT = "true";
	private static final String FALSE_TEXT = "false";
	private static final String NULL_TEXT = "unknown";
	public void setUp()
	{
		tester = new BooleanToStringConverterTester();
	}
	@Test
	public void givenSourceValues_whenApplyingConverters_thenConvertedResults()
	{
		tester.givenSourceValues(creatBooleanList(true, false, null));
		
		tester.whenApplyingConverters();
		
		tester.assertConvertedResults(createStringList(TRUE_TEXT, FALSE_TEXT, NULL_TEXT));
	}
	@Test
	public void givenEmptySourceValuesAndConverters_whenSettingSourceValues_thenConvertedResults()
	{
		tester.givenSourceValues(creatBooleanList(null, null, null));
		tester.applyConverters();
		
		tester.whenSettingSourceValues(creatBooleanList(true, false, null));
		
		tester.assertConvertedResults(createStringList(TRUE_TEXT, FALSE_TEXT, NULL_TEXT));
	}
	private List<String> createStringList(String... strs)
	{
		return Lists.newArrayList(strs);
	}
	private List<Boolean> creatBooleanList(Boolean... values)
	{
		return AbstractConverterTester.creatBooleanList(values);
	}
	private static class BooleanToStringConverterTester extends AbstractConverterTester<Boolean, String>
	{
		@Override
		protected ValueModel<String> createConverter(ValueModel<Boolean> source)
		{
			return Converters.createBooleanToStringConverter(source, TRUE_TEXT, FALSE_TEXT, NULL_TEXT);
		}
		@Override
		protected ValueModel<Boolean> createSourceValueModel(Boolean sourceValue)
		{
			return ValueHolders.create(sourceValue);
		}
	}
}
