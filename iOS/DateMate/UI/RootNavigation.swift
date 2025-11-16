//
//  RootNavigation.swift
//  DateMate
//
//  Created by 홍희표 on 2025/11/12.
//

import SwiftUI

struct RootNavigation: View {
    @State private var path: [String] = []
    
    @State var isLoggedIn: Bool = false

    var body: some View {
        Group {
            if isLoggedIn {
                NavigationStack(path: $path) {
                    MainView2(
                        onNavigateToSubFirst: { data in
                            path.append("sub_first:\(data)")
                        },
                        onNavigateToSubSecond: {
                            path.append("sub_second")
                        }
                    )
                    .navigationDestination(for: String.self) { route in
                        if route.starts(with: "sub_first:") {
                            let data = route.replacingOccurrences(of: "sub_first:", with: "")
                            SubFirstView(viewModel: SubFirstViewModel(data: data))
                        } else if route == "sub_second" {
                            SubSecondView(
                                //onNavigateUp = { path.removeLast() }
                            )
                        }
                    }
                }
            } else {
                NavigationStack(path: $path) {
                    SignInView2(
                        onSignUp: {
                            path.append("sign_up")
                        }
                    )
                    .navigationDestination(for: String.self) { route in
                        if route == "sign_up" {
                            SignUpView2 {
                                path = []
                            }
                        }
                    }
                }
            }
        }
        .onReceive(DependencyContainer.instance.isLoggedInPublisher) {
            isLoggedIn = $0
        }
    }
}

/*struct RootNavigation: View {
    @State private var path: [String] = []

    var body: some View {
        NavigationStack(path: $path) {
            MainView2(onNavigate: { route in
                path.append(route)
            })
            .navigationDestination(for: String.self) { route in
                switch route {
                case let str where str.starts(with: "sub_first/"):
                    let data = str.replacingOccurrences(of: "sub_first/", with: "")
                    SubFirstView(viewModel: SubFirstViewModel(data: data))
                case "sub_second":
                    SubSecondView()
                default:
                    EmptyView()
                }
            }
        }
    }
}*/

struct RootNavigation_Previews: PreviewProvider {
    static var previews: some View {
        RootNavigation()
    }
}
