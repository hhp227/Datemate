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
