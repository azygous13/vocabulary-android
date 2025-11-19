package com.vocabulary.data

import com.vocabulary.data.model.DifficultyLevel
import com.vocabulary.data.model.Word
import com.vocabulary.data.model.WordCategory

object SampleData {
    fun getSampleWords(): List<Word> = listOf(
        // Beginner words
        Word(
            word = "Happy",
            pronunciation = "/ˈhæp.i/",
            partOfSpeech = "adjective",
            definition = "Feeling or showing pleasure or contentment",
            example = "She was very happy to see her old friend.",
            synonyms = "joyful, cheerful, delighted, pleased",
            antonyms = "sad, unhappy, miserable",
            etymology = "From Middle English hap (chance, fortune)",
            difficultyLevel = DifficultyLevel.BEGINNER.name,
            category = WordCategory.GENERAL.name
        ),
        Word(
            word = "Courage",
            pronunciation = "/ˈkɜːr.ɪdʒ/",
            partOfSpeech = "noun",
            definition = "The ability to do something that frightens one; bravery",
            example = "It takes courage to stand up for what you believe in.",
            synonyms = "bravery, valor, fearlessness, boldness",
            antonyms = "cowardice, fear, timidity",
            etymology = "From Old French corage (heart, innermost feelings)",
            difficultyLevel = DifficultyLevel.BEGINNER.name,
            category = WordCategory.GENERAL.name
        ),
        Word(
            word = "Knowledge",
            pronunciation = "/ˈnɒl.ɪdʒ/",
            partOfSpeech = "noun",
            definition = "Facts, information, and skills acquired through experience or education",
            example = "She has extensive knowledge of computer programming.",
            synonyms = "understanding, wisdom, learning, expertise",
            antonyms = "ignorance, unawareness",
            etymology = "From Middle English knowleche",
            difficultyLevel = DifficultyLevel.BEGINNER.name,
            category = WordCategory.ACADEMIC.name
        ),

        // Intermediate words
        Word(
            word = "Eloquent",
            pronunciation = "/ˈel.ə.kwənt/",
            partOfSpeech = "adjective",
            definition = "Fluent or persuasive in speaking or writing",
            example = "The speaker gave an eloquent presentation on climate change.",
            synonyms = "articulate, fluent, persuasive, expressive",
            antonyms = "inarticulate, tongue-tied",
            etymology = "From Latin eloquens (speaking out)",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.GENERAL.name
        ),
        Word(
            word = "Perseverance",
            pronunciation = "/ˌpɜː.sɪˈvɪə.rəns/",
            partOfSpeech = "noun",
            definition = "Persistence in doing something despite difficulty or delay in achieving success",
            example = "Her perseverance in learning English paid off.",
            synonyms = "persistence, determination, tenacity, dedication",
            antonyms = "inconstancy, irresolution",
            etymology = "From Old French perseverance",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.GENERAL.name
        ),
        Word(
            word = "Innovative",
            pronunciation = "/ˈɪn.ə.veɪ.tɪv/",
            partOfSpeech = "adjective",
            definition = "Featuring new methods; advanced and original",
            example = "The company developed an innovative solution to reduce waste.",
            synonyms = "creative, original, novel, groundbreaking",
            antonyms = "conventional, traditional, unoriginal",
            etymology = "From Latin innovare (to renew, restore)",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.TECHNOLOGY.name
        ),
        Word(
            word = "Analyze",
            pronunciation = "/ˈæn.əl.aɪz/",
            partOfSpeech = "verb",
            definition = "Examine methodically and in detail for purposes of explanation and interpretation",
            example = "Scientists analyze data to draw conclusions.",
            synonyms = "examine, study, investigate, scrutinize",
            antonyms = "synthesize, combine",
            etymology = "From Greek analysis (a breaking up)",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.ACADEMIC.name
        ),

        // Advanced words
        Word(
            word = "Ephemeral",
            pronunciation = "/ɪˈfem.ər.əl/",
            partOfSpeech = "adjective",
            definition = "Lasting for a very short time",
            example = "The ephemeral nature of social media trends is well documented.",
            synonyms = "transient, fleeting, momentary, temporary",
            antonyms = "permanent, eternal, lasting",
            etymology = "From Greek ephemeros (lasting only a day)",
            difficultyLevel = DifficultyLevel.ADVANCED.name,
            category = WordCategory.GENERAL.name
        ),
        Word(
            word = "Ubiquitous",
            pronunciation = "/juːˈbɪk.wɪ.təs/",
            partOfSpeech = "adjective",
            definition = "Present, appearing, or found everywhere",
            example = "Smartphones have become ubiquitous in modern society.",
            synonyms = "omnipresent, pervasive, universal, everywhere",
            antonyms = "rare, scarce, uncommon",
            etymology = "From Latin ubique (everywhere)",
            difficultyLevel = DifficultyLevel.ADVANCED.name,
            category = WordCategory.TECHNOLOGY.name
        ),
        Word(
            word = "Paradigm",
            pronunciation = "/ˈpær.ə.daɪm/",
            partOfSpeech = "noun",
            definition = "A typical example or pattern of something; a model",
            example = "The discovery shifted the scientific paradigm.",
            synonyms = "model, pattern, example, prototype",
            antonyms = "anomaly, exception",
            etymology = "From Greek paradeigma (pattern, example)",
            difficultyLevel = DifficultyLevel.ADVANCED.name,
            category = WordCategory.ACADEMIC.name
        ),
        Word(
            word = "Meticulous",
            pronunciation = "/məˈtɪk.jə.ləs/",
            partOfSpeech = "adjective",
            definition = "Showing great attention to detail; very careful and precise",
            example = "The artist was meticulous in her work.",
            synonyms = "careful, precise, thorough, scrupulous",
            antonyms = "careless, sloppy, negligent",
            etymology = "From Latin meticulosus (fearful)",
            difficultyLevel = DifficultyLevel.ADVANCED.name,
            category = WordCategory.GENERAL.name
        ),

        // Expert words
        Word(
            word = "Obfuscate",
            pronunciation = "/ˈɒb.fə.skeɪt/",
            partOfSpeech = "verb",
            definition = "To make obscure, unclear, or unintelligible",
            example = "The lawyer tried to obfuscate the facts of the case.",
            synonyms = "obscure, confuse, muddle, complicate",
            antonyms = "clarify, explain, illuminate",
            etymology = "From Latin obfuscare (to darken)",
            difficultyLevel = DifficultyLevel.EXPERT.name,
            category = WordCategory.LEGAL.name
        ),
        Word(
            word = "Serendipity",
            pronunciation = "/ˌser.ənˈdɪp.ə.ti/",
            partOfSpeech = "noun",
            definition = "The occurrence of events by chance in a happy or beneficial way",
            example = "It was pure serendipity that led to the discovery of penicillin.",
            synonyms = "chance, luck, fortune, coincidence",
            antonyms = "misfortune, unluckiness",
            etymology = "Coined by Horace Walpole from the Persian fairy tale 'The Three Princes of Serendip'",
            difficultyLevel = DifficultyLevel.EXPERT.name,
            category = WordCategory.GENERAL.name
        ),
        Word(
            word = "Quintessential",
            pronunciation = "/ˌkwɪn.təˈsen.ʃəl/",
            partOfSpeech = "adjective",
            definition = "Representing the most perfect or typical example of a quality or class",
            example = "She is the quintessential professional.",
            synonyms = "典型的, ideal, perfect, model",
            antonyms = "atypical, unusual",
            etymology = "From Latin quinta essentia (fifth essence)",
            difficultyLevel = DifficultyLevel.EXPERT.name,
            category = WordCategory.GENERAL.name
        ),
        Word(
            word = "Esoteric",
            pronunciation = "/ˌes.əˈter.ɪk/",
            partOfSpeech = "adjective",
            definition = "Intended for or likely to be understood by only a small number of people with specialized knowledge",
            example = "The professor's lecture was filled with esoteric references.",
            synonyms = "obscure, arcane, abstruse, cryptic",
            antonyms = "simple, straightforward, exoteric",
            etymology = "From Greek esoterikos (belonging to an inner circle)",
            difficultyLevel = DifficultyLevel.EXPERT.name,
            category = WordCategory.ACADEMIC.name
        ),
        Word(
            word = "Ameliorate",
            pronunciation = "/əˈmiː.li.ə.reɪt/",
            partOfSpeech = "verb",
            definition = "To make something bad or unsatisfactory better",
            example = "The new policies helped ameliorate working conditions.",
            synonyms = "improve, better, enhance, upgrade",
            antonyms = "worsen, deteriorate, aggravate",
            etymology = "From Latin meliorare (to make better)",
            difficultyLevel = DifficultyLevel.EXPERT.name,
            category = WordCategory.GENERAL.name
        ),

        // Business words
        Word(
            word = "Synergy",
            pronunciation = "/ˈsɪn.ə.dʒi/",
            partOfSpeech = "noun",
            definition = "The interaction of elements that when combined produce a total effect greater than the sum of individual elements",
            example = "The merger created synergy between the two companies.",
            synonyms = "cooperation, collaboration, teamwork",
            antonyms = "discord, conflict",
            etymology = "From Greek synergia (joint work)",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.BUSINESS.name
        ),
        Word(
            word = "Leverage",
            pronunciation = "/ˈlev.ər.ɪdʒ/",
            partOfSpeech = "noun, verb",
            definition = "The power to influence or the use of something to maximum advantage",
            example = "The company used its market position to leverage better deals.",
            synonyms = "influence, power, advantage, force",
            antonyms = "disadvantage, weakness",
            etymology = "From lever + -age",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.BUSINESS.name
        ),

        // Science words
        Word(
            word = "Catalyst",
            pronunciation = "/ˈkæt.əl.ɪst/",
            partOfSpeech = "noun",
            definition = "A substance that increases the rate of a chemical reaction; something that causes change",
            example = "The discovery was a catalyst for further research.",
            synonyms = "stimulus, spark, trigger, impetus",
            antonyms = "deterrent, hindrance",
            etymology = "From Greek katalysis (dissolution)",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.SCIENCE.name
        ),
        Word(
            word = "Hypothesis",
            pronunciation = "/haɪˈpɒθ.ə.sɪs/",
            partOfSpeech = "noun",
            definition = "A proposed explanation made on the basis of limited evidence as a starting point for further investigation",
            example = "Scientists test their hypothesis through experiments.",
            synonyms = "theory, proposition, thesis, supposition",
            antonyms = "fact, certainty, proof",
            etymology = "From Greek hypothesis (foundation, base)",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.SCIENCE.name
        ),

        // Medical words
        Word(
            word = "Diagnosis",
            pronunciation = "/ˌdaɪ.əgˈnəʊ.sɪs/",
            partOfSpeech = "noun",
            definition = "The identification of the nature of an illness or problem by examination of symptoms",
            example = "The doctor gave a diagnosis of the flu.",
            synonyms = "identification, detection, recognition",
            antonyms = "prognosis",
            etymology = "From Greek diagnosis (a discerning, distinguishing)",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.MEDICAL.name
        ),

        // Arts words
        Word(
            word = "Aesthetic",
            pronunciation = "/iːsˈθet.ɪk/",
            partOfSpeech = "adjective",
            definition = "Concerned with beauty or the appreciation of beauty",
            example = "The building has great aesthetic appeal.",
            synonyms = "artistic, beautiful, tasteful, elegant",
            antonyms = "ugly, unattractive",
            etymology = "From Greek aisthētikos (sensitive, perceptive)",
            difficultyLevel = DifficultyLevel.INTERMEDIATE.name,
            category = WordCategory.ARTS.name
        )
    )
}
