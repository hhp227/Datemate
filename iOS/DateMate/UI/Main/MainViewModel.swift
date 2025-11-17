//
//  MainViewModel.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/16.
//

import Foundation

class MainViewModel: ObservableObject {
    let isLoggedIn = DependencyContainer.instance.isLoggedInPublisher
}
