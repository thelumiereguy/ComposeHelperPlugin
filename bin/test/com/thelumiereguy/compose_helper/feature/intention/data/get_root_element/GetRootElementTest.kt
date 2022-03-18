package com.thelumiereguy.compose_helper.feature.intention.data.get_root_element

import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase
import com.thelumiereguy.compose_helper.intention.data.get_root_element.GetRootElement
import junit.framework.TestCase
import org.intellij.lang.annotations.Language
import org.jetbrains.kotlin.psi.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class GetRootElementTest : LightJavaCodeInsightFixtureTestCase() {

    private val getRootElement = GetRootElement()

    @Test
    fun `when a name reference expression is selected , but root is a property , the property should be returned as root element`() {
        val ktPsiFactory = KtPsiFactory(project)

        @Language("Kotlin")
        val template = """
            val systemUiController = rememberSystemUiController()
            """.trimIndent()
            .trim()

        val file = ktPsiFactory.createFile(template)

        val property = file.lastChild as KtProperty

        val selectedItem = (property.lastChild as KtCallExpression).firstChild as? KtNameReferenceExpression

        selectedItem?.let {
            TestCase.assertEquals(property, getRootElement(it))
        }
    }


    @Test
    fun `when a name reference expression is selected, with a call expression as root, call expression should be returned`() {
        val ktPsiFactory = KtPsiFactory(project)

        @Language("Kotlin")
        val template = """
            @Composable
            fun Box(block:()->Unit) {
                
            }
            
            fun OuterComposable() {
              // Call Expression - Box
              // |
              // v
                Box() { 
                 
                } 

            }
            """.trimIndent().trim()

        val file = ktPsiFactory.createFile(template)

        val ktNamedFunction = file.lastChild as KtNamedFunction

        val callExpression = ktNamedFunction.lastChild.children.find { it is KtCallExpression }!!

        TestCase.assertEquals(
            callExpression,
            getRootElement(callExpression.firstChild as KtNameReferenceExpression)
        )
    }


    @Test
    fun `when an argument list element is selected, with a call expression as root, call expression should be returned`() {
        val ktPsiFactory = KtPsiFactory(project)

        @Language("Kotlin")
        val template = """
            @Composable
            fun Box(block:()->Unit) {
                
            }
            
            fun OuterComposable() {
                // Argument List Element - (
                // |
                // v
                Box() {
                 
                } // Name Reference Expression

            }
            """.trimIndent().trim()

        val file = ktPsiFactory.createFile(template)

        val ktNamedFunction = file.lastChild as KtNamedFunction

        val callExpression = ktNamedFunction.lastChild.children.find { it is KtCallExpression }!!

        val argumentListElement = callExpression.firstChild.nextSibling as KtValueArgumentList

        TestCase.assertEquals(
            callExpression,
            getRootElement(argumentListElement)
        )
    }


    @Test
    fun `when a name reference expression is selected, with a delegated property as root, property should be returned`() {
        val ktPsiFactory = KtPsiFactory(project)

        @Language("Kotlin")
        val template = """
                               // Delegated porperty - by remember
                               // |
                               // v
           var isComposable by remember {
               true
            }
            """.trimIndent().trim()

        val file = ktPsiFactory.createFile(template)

        val property = file.lastChild as KtProperty

        val referenceExpression = property.lastChild.lastChild.firstChild as KtNameReferenceExpression

        TestCase.assertEquals(
            property,
            getRootElement(referenceExpression)
        )
    }

    @Test
    fun `when a name reference expression is selected, with a delegated property with dot qualified expression as root, property should be returned`() {
        val ktPsiFactory = KtPsiFactory(project)

        @Language("Kotlin")
        val template = """
          val repeatingAnimation = rememberInfiniteTransition()

                                            // Dot qualified expression - .animateFloat
                                            // |
                                            // v
          val offset by repeatingAnimation.animateFloat(
              0f,
              -20f,
              infiniteRepeatable(
                  repeatMode = RepeatMode.Reverse,
                  animation = tween(
                      durationMillis = 1000,
                      easing = LinearEasing
                  )
              )
          )
          """.trimIndent().trim()

        val file = ktPsiFactory.createFile(template)

        val property = file.lastChild as KtProperty

        val dotQualifiedExpression = property.lastChild.lastChild as KtDotQualifiedExpression

        val referenceExpression = dotQualifiedExpression.lastChild.firstChild as KtNameReferenceExpression

        TestCase.assertEquals(
            property,
            getRootElement(referenceExpression)
        )
    }


    @Test
    fun `when a name reference expression is selected, with a property and dot qualified expression as root, property should be returned`() {
        val ktPsiFactory = KtPsiFactory(project)

        @Language("Kotlin")
        val template = """
          val repeatingAnimation = rememberInfiniteTransition()

                                            // Dot qualified expression - .animateFloat
                                            // |
                                            // v
          val offset = repeatingAnimation.animateFloat(
              0f,
              -20f,
              infiniteRepeatable(
                  repeatMode = RepeatMode.Reverse,
                  animation = tween(
                      durationMillis = 1000,
                      easing = LinearEasing
                  )
              )
          )
          """.trimIndent().trim()

        val file = ktPsiFactory.createFile(template)

        val property = file.lastChild as KtProperty

        val dotQualifiedExpression = property.lastChild as KtDotQualifiedExpression

        val referenceExpression = dotQualifiedExpression.lastChild.firstChild as KtNameReferenceExpression

        TestCase.assertEquals(
            property,
            getRootElement(referenceExpression)
        )
    }

}
