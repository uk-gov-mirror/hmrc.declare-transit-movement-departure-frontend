/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models.journeyDomain

import java.time.LocalDateTime

import base.{GeneratorSpec, SpecBase}
import cats.data.NonEmptyList
import generators.JourneyModelGenerators
import models.DeclarationType.Option4
import models.GuaranteeType.{GuaranteeNotRequired, IndividualGuarantee}
import models.ProcedureType.Normal
import models.domain.{Address, SealDomain}
import models.journeyDomain.GoodsSummary.GoodSummaryNormalDetails
import models.journeyDomain.GuaranteeDetails.{GuaranteeOther, GuaranteeReference}
import models.journeyDomain.ItemTraderDetails.RequiredDetails
import models.journeyDomain.ItemsSecurityTraderDetails.SecurityPersonalInformation
import models.journeyDomain.JourneyDomainSpec.woa2
import models.journeyDomain.MovementDetails.{DeclarationForSelf, NormalMovementDetails}
import models.journeyDomain.Packages.BulkPackages
import models.journeyDomain.RouteDetails.TransitInformation
import models.journeyDomain.TraderDetails.{PersonalInformation, TraderInformation}
import models.journeyDomain.TransportDetails.DetailsAtBorder.NewDetailsAtBorder
import models.journeyDomain.TransportDetails.InlandMode.Mode5or7
import models.journeyDomain.TransportDetails.ModeCrossingBorder.ModeWithNationality
import models.{EoriNumber, LocalReferenceNumber, UserAnswers}
import models.reference.{Country, CountryCode, CustomsOffice, PackageType}

class JourneyDomainSpec extends SpecBase with GeneratorSpec with JourneyModelGenerators {

  "JourneyDomain" - {
//    "can be parsed UserAnswers" - {
//      "when all details for section have been answered" in {
//        forAll(arb[JourneyDomain]) {
//          journeyDomain =>
//            val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(journeyDomain)(emptyUserAnswers)
//
//            println(s"\n\n ${updatedUserAnswer.data} \n\n")
//            val result = UserAnswersReader[JourneyDomain].run(updatedUserAnswer)
//
//            result.value.preTaskList mustEqual journeyDomain.preTaskList
//            result.value.movementDetails mustEqual journeyDomain.movementDetails
//            result.value.routeDetails mustEqual journeyDomain.routeDetails
//            result.value.transportDetails mustEqual journeyDomain.transportDetails
//            result.value.traderDetails mustEqual journeyDomain.traderDetails
//            result.value.goodsSummary mustEqual journeyDomain.goodsSummary
//            result.value.guarantee mustEqual journeyDomain.guarantee
//            result.value.safetyAndSecurity mustEqual journeyDomain.safetyAndSecurity
//        }
//      }
//    }

    "this should fail" in {

      val updatedUserAnswer = JourneyDomainSpec.setJourneyDomain(woa2)(emptyUserAnswers)

      val result = UserAnswersReader[JourneyDomain].run(updatedUserAnswer)

      result.value.preTaskList mustEqual woa2.preTaskList
      result.value.movementDetails mustEqual woa2.movementDetails
      result.value.routeDetails mustEqual woa2.routeDetails
      result.value.transportDetails mustEqual woa2.transportDetails
      result.value.traderDetails mustEqual woa2.traderDetails
      result.value.goodsSummary mustEqual woa2.goodsSummary
      result.value.guarantee mustEqual woa2.guarantee
      result.value.safetyAndSecurity mustEqual woa2.safetyAndSecurity
    }

//    "cannot be parsed" - {
//      "when some answers is missing" in {
//        forAll(arb[ItemSection], arb[UserAnswers]) {
//          case (itemSection, ua) =>
//            val userAnswers                 = ItemDetailsSpec.setItemDetailsUserAnswers(itemSection.itemDetails, index)(ua)
//            val result: Option[ItemSection] = ItemSection.readerItemSection(index).run(userAnswers)
//
//            result mustBe None
//        }
//      }
//    }
  }
}

object JourneyDomainSpec {

  def setJourneyDomain(journeyDomain: JourneyDomain)(startUserAnswers: UserAnswers): UserAnswers =
    (
      PreTaskListDetailsSpec.setPreTaskListDetails(journeyDomain.preTaskList) _ andThen
        RouteDetailsSpec.setRouteDetails(journeyDomain.routeDetails) andThen
        TransportDetailsSpec.setTransportDetail(journeyDomain.transportDetails) andThen
        ItemSectionSpec.setItemSections(journeyDomain.itemDetails.toList) andThen
        GoodsSummarySpec.setGoodsSummary(journeyDomain.goodsSummary) andThen
        GuaranteeDetailsSpec.setGuaranteeDetails(journeyDomain.guarantee) andThen
        TraderDetailsSpec.setTraderDetails(journeyDomain.traderDetails) andThen
        MovementDetailsSpec.setMovementDetails(journeyDomain.movementDetails) andThen
        safetyAndSecurity(journeyDomain.safetyAndSecurity)
    )(startUserAnswers)

  def safetyAndSecurity(safetyAndSecurity: Option[SafetyAndSecurity])(startUserAnswers: UserAnswers): UserAnswers =
    safetyAndSecurity match {
      case Some(value) => {
        println(s"\n\n\n SET THIS THING HERE $safetyAndSecurity))))))))))))))))))))))))))))))))")
        SafetyAndSecuritySpec.setSafetyAndSecurity(value)(startUserAnswers)
      }
      case None =>
        println(s"\n\n\n DIDNT SET THIS THING HERE ))))))))))))))))))))))))))))))))")
        startUserAnswers
    }

  val woa2 = JourneyDomain(
    PreTaskListDetails(LocalReferenceNumber("h2jXda9dTmqyomkpqrsfq").get, Normal, true),
    NormalMovementDetails(Option4, true, true, "袲鉅쁆梎뭹㪊䁯驹ঌ굎旵톾賂碑䯛騹츺犍朢黛ʥ뺖睓⽋㩺쬯騯", DeclarationForSelf),
    RouteDetails(
      CountryCode("RS"),
      CustomsOffice("⺝ꘀࠍꈹ㄃휔㟉鄽됤㟖犯岸誝┄墜喙欿", "끍贮朦ﲽ瑂⹣銢㈉聞坉㖕๡恻顋㤖ൽ㫌㧴", CountryCode("AD"), List(), Some("ꠑభ酃ֱ툒བྷ裁熎밝ᅰᅕ啴皴輔낫㛺ꦒ覀렧鉉￙抟ᙬ諦䢄ၿ啑")),
      CountryCode("MC"),
      CustomsOffice("꾛櫓矒혁颼櫬", "㷍禒ÜẸ譐쯎ꕷ⸶幥ֺ꫉䍙쇪媻鏤ᔩꍢ㽗슑㈞溊⧍ވ栃彺譫", CountryCode("DE"), List(), Some("硦晰싚뢒텪㱟㖹쥒鷩⯩열ೄ缾⥽醼蕛岼늩梟.ꅲ⻚蛜䃵쪰쨻ﶩₖḋ膒墑ꓭ⾷랷")),
      NonEmptyList(
        TransitInformation("ຎ꬚ᥟ甧ị㔑興繻裹赣랽㕠軎ඩ䖓펛", Some(LocalDateTime.parse("2084-10-27T13:03"))),
        List(TransitInformation("ꛧ﹏녽㹄", Some(LocalDateTime.parse("2084-10-27T13:03"))))
      )
    ),
    TransportDetails(Mode5or7(7), NewDetailsAtBorder("334996152", "靊뀕潝禫㲗ᯋᤴ蘆桀", ModeWithNationality(CountryCode("PJ"), -2147483648))),
    TraderDetails(
      PersonalInformation("ꈒை㸫ꤘ捉熥⬗ﺔᅾ⾙劜跂ṃ霴䰱⚘꧝ክ屵䧤㦹ꉸ棩", Address("又奞蕽扲㢔ﵳ汵졳묝弳", "詺郭ⓟ諝漱弄㨌謙胥宷轲쏐䌗䜄鯍", "窣腖㭜", None)),
      Some(
        TraderInformation(
          "娴吽駕ꕑᩞ㯂丘뻽熏ꥴਧ়䵋㏆煱㔹㨃舍揢㝦☊龥ባ줫胇ꍆ罬㨪Ħ圕蜋",
          Address("琎ᜓಓ觋麴腉꟞沙●壽䯥찘껏宦", "忪㼰﫡槒펾韔㍕ꕻ顮畣웯ꔬ", "눈畈亃税哉ࡗ᎟졬䶦ꑥ䯬걎ೠ鼶꣆圝妠뽢췈倾ᷢ꫻록뎯傝೶묡㭣᫤膔⎙ঈ椔葉䊬", Some(Country(CountryCode("MT"), "箱ﬔ咞땛友瀁⪘瞁⮎啺ꟃ"))),
          None
        )),
      Some(
        TraderInformation(
          "䭣ꗤᵏ蘖룪횈燁謵ꙙ⩵",
          Address("⋣慆ሹⱃऀ헜訰樞藝䀯⌋⸉ᕟ噶ㅦď⦿鋜癟킰뻍㮬컭囩囤쁥Ქ", "뾐┖칙ႀￊ⪯岻㙜叄シ鲶⺓ꃎ֛萼㉏˨〳䎗앲ᢎ㔪瓠૙ꁺ両奄恿诚郄", "Ά뗍艗귘胑总䪿℈꟰堙̘", Some(Country(CountryCode("MU"), "죠䋎"))),
          Some(EoriNumber("䃏嗘狽艐"))
        ))
    ),
    NonEmptyList(
      ItemSection(
        ItemDetails("苹☘", "0", Some("1"), Some("譸")),
        None,
        Some(
          RequiredDetails("뜚ꙶ烔퉂",
                          Address("먍酛剓縨ꞟ⯝ࣜⴃ헺", "㡙踋콚쾂ꁲ", "捈Ҁ埯覦泖旿亍穹킎ⱇ빪䑮藄ᆸ峌玤鼌函", Some(Country(CountryCode("FQ"), "﹧ᩣ䴝颽塛椲ꕅḗ"))),
                          Some(EoriNumber("")))),
        NonEmptyList(BulkPackages(PackageType("䜖셆ᛠ⁷ꯜἑ唉뵼钮偡兖沼墆डᠦ햅㊓⃊泽ᅄ咯ⲿ軃넧면扖縧쿗", "(VR)"), Some(2), Some("⛄؜넶䖩穐㮷誢䰎鼌痈ꁏ胏间崜ⷴ臮쳧㤧璷ꙛ焐鴇㘁蜆¾낧㿛ි㻱☀ɿ꺎")),
                     List.empty),
        Some(NonEmptyList(Container("ﵖ쨅"), List.empty)),
        Some(NonEmptyList(SpecialMention("⣭ʢబ䤁坤챴ɢ墪錺᝔✗좏쀗뗡똉하ᡌ嫾Ⅵ祙", "㉦겪淂⏻﫜죏ㄫ饱섌뜟罞✐飽㚾ೃ쑧鑉ﯩꛎＵ"), List.empty)),
        None,
        Some(
          ItemsSecurityTraderDetails(
            None,
            Some("腍瀄嫕ᗪ훏鯎싷숏⁼䘹訠懟낉洝ꭚ쌓곦蒅ᬓ쪴遝ꉂੱᔢ琻廚韢㳯脙匤᧭ﾻ냚㡩託㢩㠭읷"),
            Some("㠵屜셯䆎늗ﵨ혃ᛥ䒌⅜嶶宿㣼⡘옏鿀ฮ䞬쵘活烑鵛Ć"),
            Some(SecurityPersonalInformation("゜ᄪ๢ᛤ遢䇄蒼ꯤⲞꟶꉉ緛萀鳉䆐㧈凛୍ⓗ鈜묬",
                                             Address("⏿撽㗏찹鬞ꡯ஗ϲﲟ갉䖠ﳊ騡洛䞹쬿ꋑ찛㊙륄ͱ笤蕎뫠資߅뷮켠钴", "냼톩잧뮥", "﷋湪얗榸䣸揲慒ᦗ", Some(Country(CountryCode("SI"), "ဈ騐굓뒜"))))),
            Some(SecurityPersonalInformation(
              "㝀䎍糑焆﷔늄ĺᒚꭝ˂ﵾ鼩꧀漟灕睭穐ʆ秺踆迍ӽ䜦㶻冇Ｉ䆫຺㽺",
              Address("䑈岝㛘衊ဨ도뛉ꢫ",
                      "ਖ씭犟꼍鮿ໂ諃㵳ꁋ➫煌鐱깰᭷ꉏ䚿⪋॑ꝁ忀㔠㫉꒠Ţﳁ팢켺㘤虗㤕䅩㨴뎂",
                      "忛볁토頲त죓糩킑혙驏ܠ퐞郗쏔롾ᖿࡕ堡昀쳧⛟ᅂ〿瘣ﳀᠳ ⶗໿",
                      Some(Country(CountryCode("LF"), "롱ﶌݖ깎ꔁ袱웷וֹ팮༡瀩逛鵎ꃝ秽㐖३嫜鑂蝗癭")))
            ))
          )),
        None
      ),
      List(
        ItemSection(
          ItemDetails("苹☘", "0", Some("1"), Some("譸")),
          None,
          Some(
            RequiredDetails("뜚ꙶ烔퉂",
                            Address("먍酛剓縨ꞟ⯝ࣜⴃ헺", "㡙踋콚쾂ꁲ", "捈Ҁ埯覦泖旿亍穹킎ⱇ빪䑮藄ᆸ峌玤鼌函", Some(Country(CountryCode("FQ"), "﹧ᩣ䴝颽塛椲ꕅḗ"))),
                            Some(EoriNumber("")))),
          NonEmptyList(BulkPackages(PackageType("䜖셆ᛠ⁷ꯜἑ唉뵼钮偡兖沼墆डᠦ햅㊓⃊泽ᅄ咯ⲿ軃넧면扖縧쿗", "VR"), Some(2), Some("⛄؜넶䖩穐㮷誢䰎鼌痈ꁏ胏间崜ⷴ臮쳧㤧璷ꙛ焐鴇㘁蜆¾낧㿛ි㻱☀ɿ꺎")),
                       List.empty),
          Some(NonEmptyList(Container("ﵖ쨅"), List.empty)),
          Some(NonEmptyList(SpecialMention("⣭ʢబ䤁坤챴ɢ墪錺᝔✗좏쀗뗡똉하ᡌ嫾Ⅵ祙", "㉦겪淂⏻﫜죏ㄫ饱섌뜟罞✐飽㚾ೃ쑧鑉ﯩꛎＵ"), List.empty)),
          None,
          Some(ItemsSecurityTraderDetails(
            None,
            Some("腍瀄嫕ᗪ훏鯎싷숏⁼䘹訠懟낉洝ꭚ쌓곦蒅ᬓ쪴遝ꉂੱᔢ琻廚韢㳯脙匤᧭ﾻ냚㡩託㢩㠭읷"),
            Some("㠵屜셯䆎늗ﵨ혃ᛥ䒌⅜嶶宿㣼⡘옏鿀ฮ䞬쵘活烑鵛Ć"),
            Some(SecurityPersonalInformation("゜ᄪ๢ᛤ遢䇄蒼ꯤⲞꟶꉉ緛萀鳉䆐㧈凛୍ⓗ鈜묬",
                                             Address("⏿撽㗏찹鬞ꡯ஗ϲﲟ갉䖠ﳊ騡洛䞹쬿ꋑ찛㊙륄ͱ笤蕎뫠資߅뷮켠钴", "냼톩잧뮥", "湪얗榸䣸揲慒ᦗ", Some(Country(CountryCode("SI"), "ဈ騐굓뒜"))))),
            Some(SecurityPersonalInformation(
              "㝀䎍糑焆﷔늄ĺᒚꭝ˂ﵾ鼩꧀漟灕睭穐ʆ秺踆迍ӽ䜦㶻冇Ｉ䆫຺㽺",
              Address("䑈岝㛘衊ဨ도뛉ꢫ",
                      "ਖ씭犟꼍鮿ໂ諃㵳ꁋ➫煌鐱깰᭷ꉏ䚿⪋॑ꝁ忀㔠㫉꒠Ţﳁ팢켺㘤虗㤕䅩㨴뎂",
                      "忛볁토頲त죓糩킑혙驏ܠ퐞郗쏔롾ᖿࡕ堡昀쳧⛟ᅂ〿瘣ﳀᠳ ⶗໿",
                      Some(Country(CountryCode("LF"), "롱ﶌݖ깎ꔁ袱웷וֹ팮༡瀩逛鵎ꃝ秽㐖३嫜鑂蝗癭")))
            ))
          )),
          None
        ))
    ),
    GoodsSummary(
      Some(5),
      "39",
      None,
      GoodSummaryNormalDetails(Some("邂쇠뙑줁킬䄝틲Л᡺Ｈߧퟵ墕")),
      List(
        SealDomain("튿頥䱵ｄ"),
        SealDomain("擃㣜驳㉳骆︼ꣷ봹芍䖀⟋焺葉隑࿘ᒠ"),
        SealDomain("罓씯䤔灉ᘈ"),
        SealDomain(""),
        SealDomain("ꤾ῏拤ꅨ"),
        SealDomain("㬺"),
        SealDomain("⏕旌푏⊘酋廼荖䊽ⶴ띱괧䞤脄")
      )
    ),
    NonEmptyList(GuaranteeOther(IndividualGuarantee, "俉ﮡꡍ縋뷆鍲∁"),
                 List(GuaranteeReference(GuaranteeNotRequired, "栽걲㺖젼薠긴ﮅ袣㝻祕㈹䥪碇뿮缘籙㎩嗳쀤㆔䢓풨덒﷪锣鿢⽚", "迈狟褕", "⍵瞄遦໌齽谐㎳ᘛ莇"))),
    Some(
      SafetyAndSecurity(
        None,
        Some("篹὆ᥕ溿ᩲ尜筁첔ꢨ⚋禘쬀褀虍⯕᧳ួ๣㬅䞨⎄댁䑯ꊢ鄾貨넳⹨Ἶ曺"),
        None,
        Some("帬儺樓깵Ӥ齄䝱ᴕ騐圶䏝焞㌎㡕컵鳟"),
        Some("䦅㲼◎戰謁ꇎ㕺ꆭ殎꼃丿鰽┚﹥梓䝦嶳黇躤뱠ꨏ樸臗"),
        Some(SafetyAndSecurity.SecurityTraderDetails("ᢒ醤愧镏炚䖾啖몋뢳⠜䡡쩈ਫ曆◰֤落龚⋚", Address("⌛旃㰌鵳", "", "", Some(Country(CountryCode("GB"), ""))))),
        Some(SafetyAndSecurity.SecurityTraderDetails("㺁ⅶ挐", Address("웓繟㨀鴟㪌闧Ⴒⷵ뽎薺徎᰾벯⺰ꗀ奉㮊兒и鋺ᔒᤇ", "", "", Some(Country(CountryCode("GB"), ""))))),
        Some(SafetyAndSecurity.SecurityTraderDetails("밶ꗕޡ纅㠓킎赟緱꟤￣꠻뜯挝竬⋯冠漑쪆搧뛎ᣂ旘퀐✋䰦",
                                                     Address("럸㟃엊ஓ屬䜖ሔ∮픘딜ఇ씲⍿ꨙ♲砋Ⓗ芢ﳾ充䢂悧ଯ", "", "", Some(Country(CountryCode("GB"), ""))))),
        NonEmptyList(Itinerary(CountryCode("IR")), List(Itinerary(CountryCode("XE")), Itinerary(CountryCode("DV"))))
      ))
  )

}
