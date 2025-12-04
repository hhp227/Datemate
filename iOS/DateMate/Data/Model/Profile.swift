//
//  Profile.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/24.
//

import Foundation
import FirebaseFirestore
import FirebaseFirestoreSwift

struct Profile: Codable, Identifiable {
    @DocumentID var id: String?     // Kotlin의 uid
    //var uid: String = ""

    let name: String
    let gender: String
    let bio: String
    let birthday: Timestamp?
    let job: String
    let photos: [String]
    let updatedAt: Timestamp?
    var ageFormatted: String {
        guard let birthday = birthday?.dateValue() else {
            return "00"
        }
        let calendar = Calendar.current
        let now = Date()
        let birthComponents = calendar.dateComponents([.year, .month, .day], from: birthday)
        let nowComponents = calendar.dateComponents([.year, .month, .day], from: now)
        
        guard let birthYear = birthComponents.year,
              let birthMonth = birthComponents.month,
              let birthDay = birthComponents.day,
              let currentYear = nowComponents.year,
              let currentMonth = nowComponents.month,
              let currentDay = nowComponents.day else {
            return "00"
        }
        
        var age = currentYear - birthYear
        
        if currentMonth < birthMonth ||
            (currentMonth == birthMonth && currentDay < birthDay) {
            age -= 1
        }
        if age < 0 { age = 0 }
        return String(format: "%02d", age)
    }
    
    init(
        //uid: String = "",
        name: String = "",
        gender: String = "",
        bio: String = "",
        birthday: Timestamp? = nil,
        job: String = "",
        photos: [String] = [],
        updatedAt: Timestamp? = nil
    ) {
        //self.uid = uid
        self.name = name
        self.gender = gender
        self.bio = bio
        self.birthday = birthday
        self.job = job
        self.photos = photos
        self.updatedAt = updatedAt
    }
}
