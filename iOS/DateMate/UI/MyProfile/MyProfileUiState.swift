//
//  MyProfileUiState.swift
//  DateMate
//
//  Created by 홍희표 on 2025/12/01.
//

import Foundation

struct MyProfileUiState {
    var isLoading: Bool = false
    var profile: Profile? = nil
    var stats: UserStats = UserStats(postCount: 0, followers: "", following: "")
    var posts: [Post] = []
    var message: String? = nil
}

struct UserStats {
    var postCount: Int
    var followers: String
    var following: String
}
