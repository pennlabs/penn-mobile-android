package com.pennapps.labs.pennmobile.studentresources

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Church
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Data models for the UA Student Resources guide.
 * Content sourced from: https://sites.google.com/view/undergraduate-assembly/home
 */

data class ResourceCategory(
    val id: String,
    val title: String,
    val icon: ImageVector,
    val resources: List<Resource>,
)

data class Resource(
    val name: String,
    val description: String,
    val contacts: List<Contact> = emptyList(),
    val website: String? = null,
    val locationUrl: String? = null,
    val bullets: List<String> = emptyList(),
)

/**
 * A phone contact. [label] is what shows on the chip
 * (e.g. "Call (215) 898-7021 (24/7 help)"). [number] is the
 * dial-able digits only (e.g. "2158987021").
 */
data class Contact(
    val label: String,
    val number: String,
)

object StudentResourcesContent {
    val categories: List<ResourceCategory> =
        listOf(
            ResourceCategory(
                id = "mental_health",
                title = "Mental Health & Wellness",
                icon = Icons.Filled.Psychology,
                resources =
                    listOf(
                        Resource(
                            name = "Counseling and Professional Services (CAPS)",
                            description =
                                "Support for stress, anxiety, depression, relationship " +
                                    "problems, identity issues, crisis care, therapy, and medications.",
                            contacts =
                                listOf(
                                    Contact("Call (215) 898-7021 (24/7 help)", "2158987021"),
                                ),
                            website = "https://wellness.upenn.edu/",
                            locationUrl = "https://maps.google.com/?q=Counseling+and+Psychological+Services+Penn",
                        ),
                        Resource(
                            name = "Crisis Support",
                            description = "Immediate and urgent support.",
                            contacts =
                                listOf(
                                    Contact("Call 988 (24/7 national confidential hotline)", "988"),
                                    Contact("Call 215-573-3333 (24/7 on campus help)", "2155733333"),
                                    Contact("Call 911 (24/7 off campus help)", "911"),
                                ),
                            website = "https://www.publicsafety.upenn.edu/",
                            locationUrl = "https://maps.google.com/?q=Penn+Public+Safety",
                        ),
                    ),
            ),
            ResourceCategory(
                id = "sexual_violence",
                title = "Sexual Violence & Relationship Safety",
                icon = Icons.Filled.Favorite,
                resources =
                    listOf(
                        Resource(
                            name = "Penn Violence Prevention (PVP)",
                            description =
                                "Support for sexual assault, domestic violence, stalking, " +
                                    "and harassment. Education and prevention resources.",
                            contacts = listOf(Contact("Call 215-746-2642", "2157462642")),
                            website = "https://pvp.universitylife.upenn.edu/",
                        ),
                        Resource(
                            name = "Division of Public Safety (DPS) Special Services",
                            description =
                                "Support after an incident, including accompaniment to " +
                                    "hospitals or police.",
                            contacts = listOf(Contact("Call 215-898-6600 (24/7)", "2158986600")),
                            website = "https://www.publicsafety.upenn.edu/",
                        ),
                        Resource(
                            name = "Penn Women's Center (PWC)",
                            description = "Support and advocacy for gender-based harm.",
                            contacts = listOf(Contact("Call 215-898-8611 (24/7)", "2158988611")),
                            website = "https://pwc.universitylife.upenn.edu/",
                        ),
                        Resource(
                            name = "STTOP Team (Sexual Trauma Treatment)",
                            description =
                                "Support and care for specialized trauma. Ask your CAPS " +
                                    "clinician for a referral or contact CAPS directly and request a " +
                                    "STTOP appointment.",
                            contacts = listOf(Contact("Call CAPS (215) 898-7021", "2158987021")),
                            website = "https://wellness.upenn.edu/",
                        ),
                    ),
            ),
            ResourceCategory(
                id = "safety_security",
                title = "Safety & Security",
                icon = Icons.Filled.Security,
                resources =
                    listOf(
                        Resource(
                            name = "Division of Public Safety (DPS)",
                            description =
                                "Report an emergency and/or a crime. Support for " +
                                    "non-emergency related assistance.",
                            contacts =
                                listOf(
                                    Contact("Call 215-573-3333 (24/7 help)", "2155733333"),
                                    Contact("From a campus phone: dial 5-1-1", "511"),
                                ),
                            website = "https://www.publicsafety.upenn.edu/",
                        ),
                        Resource(
                            name = "Campus Help Line",
                            description =
                                "General resources, including safety, housing, wellness, " +
                                    "and/or referrals.",
                            contacts = listOf(Contact("Call 215-898-4357 (24/7 help)", "2158984357")),
                            website = "https://www.publicsafety.upenn.edu/help-line/",
                        ),
                    ),
            ),
            ResourceCategory(
                id = "physical_health",
                title = "Physical Health",
                icon = Icons.Filled.FitnessCenter,
                resources =
                    listOf(
                        Resource(
                            name = "Student Health Service",
                            description =
                                "General medical care, urgent care, sexual and/or " +
                                    "reproductive healthcare, immunizations, sports medicine, travel " +
                                    "medicine, and/or preventative care.",
                            contacts = listOf(Contact("Call 215-746-9355 (24/7 help)", "2157469355")),
                            website = "https://wellness.upenn.edu/student-health-counseling/medical-care",
                        ),
                    ),
            ),
            ResourceCategory(
                id = "religious_spiritual",
                title = "Religious & Spiritual",
                icon = Icons.Filled.Church,
                resources =
                    listOf(
                        Resource(
                            name = "Office of the Chaplain",
                            description =
                                "Support for students, of any belief, seeking faith and " +
                                    "spiritual care.",
                            contacts = listOf(Contact("Call 215-898-8456", "2158988456")),
                            website = "https://chaplain.upenn.edu/",
                        ),
                    ),
            ),
            ResourceCategory(
                id = "tutoring_accessibility",
                title = "Tutoring and Accessibility",
                icon = Icons.Filled.MenuBook,
                resources =
                    listOf(
                        Resource(
                            name = "Weingarten Center",
                            description =
                                "Learning support for any subject. Accessibility services " +
                                    "for disabilities, learning differences, chronic or temporary health " +
                                    "conditions, concussions, or injuries.",
                            contacts =
                                listOf(
                                    Contact("Call 215-573-9235 (Academic Support)", "2155739235"),
                                    Contact("Call 267-788-0030 (Accessibility Support)", "2677880030"),
                                ),
                            website = "https://weingartencenter.universitylife.upenn.edu/",
                            bullets =
                                listOf(
                                    "Private one-on-one, group, and drop-in tutoring",
                                    "Academic accommodations (extended time, note-taking, flexible attendance, etc.)",
                                    "Learning strategies and study skills coaching",
                                    "Support for executive functioning and organization",
                                ),
                        ),
                        Resource(
                            name = "Language Center",
                            description = "Language support.",
                            website = "https://plc.sas.upenn.edu/",
                            bullets =
                                listOf(
                                    "One-on-one language consultations",
                                    "Pronunciation and presentation support",
                                    "Academic English and discipline-specific language help",
                                    "Support for writing, speaking, and listening (not grading or editing)",
                                ),
                        ),
                        Resource(
                            name = "Writing Center",
                            description = "Writing support at any stage of development.",
                            website = "https://writing.upenn.edu/critical/wc/",
                            bullets =
                                listOf(
                                    "Help at any stage, from brainstorming to polishing a final draft",
                                    "Feedback on any form of writing — essays, lab reports, theses, personal statements",
                                    "Commentary on structure, clarity, argument, and flow",
                                ),
                        ),
                    ),
            ),
            ResourceCategory(
                id = "legal",
                title = "Legal",
                icon = Icons.Filled.Gavel,
                resources =
                    listOf(
                        Resource(
                            name = "Attorney Meetings",
                            description =
                                "For Penn students seeking legal advice on housing and " +
                                    "landlord issues, food and government benefits, or healthcare " +
                                    "insurance and medical bills. Email legal@pennua.org to schedule a " +
                                    "one-on-one, in-person appointment with UA's attorney partner. Each " +
                                    "student is limited to one consultation per issue.",
                            website = "mailto:legal@pennua.org",
                        ),
                    ),
            ),
        )
}
